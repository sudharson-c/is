def is_prime(num: int) -> bool:
    """Check if a number is prime."""
    if num == 1:
        return False
    for d in range(2, num // 2):
        if num % d == 0:
            return False
    return True

def prime_divisor(num: int) -> int:
    """Find a prime divisor of num-1."""
    for i in range(2, num - 1):
        if (num - 1) % i == 0 and is_prime(i):
            return i
    return None
