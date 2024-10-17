import struct

# Constants for SHA-1
H0 = 0x67452301
H1 = 0xEFCDAB89
H2 = 0x98BADCFE
H3 = 0x10325476
H4 = 0xC3D2E1F0

# Left rotate function (circular left shift)
def left_rotate(value, shift):
    return ((value << shift) & 0xFFFFFFFF) | (value >> (32 - shift))

# Function to pad the input
def pad_message(message):
    original_length = len(message)
    original_length_bits = original_length * 8
    padding_length = (56 - (original_length + 1) % 64 + 64) % 64

    # Add 1 byte for the '1' bit, and enough bytes for padding + 8 bytes for length
    padded_message = bytearray(message)
    padded_message.append(0x80)

    # Append padding bytes
    padded_message.extend([0] * padding_length)

    # Append the original length in bits as a 64-bit big-endian integer
    padded_message.extend(struct.pack('>Q', original_length_bits))
    return padded_message

def main():
    # Get input text from the user
    input_text = input("Enter the text to hash: ")
    padded_message = pad_message(input_text.encode())

    # Ask for round number and step number
    round_num = int(input("Enter the round number (1-4): "))
    step = int(input("Enter the step number (1-79): "))

    # Ensure valid round and step numbers
    if round_num < 0 or round_num > 79 or step < 0 or step > 15:
        print("Invalid round or step number. Please enter valid values.")
        return

    # Message schedule array
    w = [0] * 80

    # Process block for just one step
    for i in range(16):
        w[i] = (padded_message[i * 4] << 24) | (padded_message[i * 4 + 1] << 16) | (padded_message[i * 4 + 2] << 8) | padded_message[i * 4 + 3]

    # Extend the sixteen 32-bit words into eighty 32-bit words
    for i in range(16, 80):
        w[i] = left_rotate(w[i - 3] ^ w[i - 8] ^ w[i - 14] ^ w[i - 16], 1)

    # Initial hash values
    a, b, c, d, e = H0, H1, H2, H3, H4

    # Determine the value of f and k based on the round number
    if round_num < 20:
        f = (b & c) | (~b & d)  # First 20 rounds
        k = 0x5A827999
    elif round_num < 40:
        f = b ^ c ^ d           # 20 to 39 rounds
        k = 0x6ED9EBA1
    elif round_num < 60:
        f = (b & c) | (b & d) | (c & d)  # 40 to 59 rounds
        k = 0x8F1BBCDC
    else:
        f = b ^ c ^ d           # 60 to 79 rounds
        k = 0xCA62C1D6

    # Perform the specific step (for the given step in the block)
    temp = (left_rotate(a, 5) + f + e + k + w[step]) & 0xFFFFFFFF
    e = d
    d = c
    c = left_rotate(b, 30)
    b = a
    a = temp

    # Output the updated hash values after the specific step
    print(f"After round {round_num} and step {step}:\n a = {a:08x}\n b = {b:08x}\n c = {c:08x}\n d = {d:08x}\n e = {e:08x}")

if __name__ == "__main__":
    main()
