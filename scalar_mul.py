# Scalar multiplication (n * G)
def scalar_multiply(P, n, a, p):
    result = None  # Point at infinity
    current = P
    while n > 0:
        if n % 2 == 1:
            result = point_add(result, current, a, p)
        current = point_add(current, current, a, p)
        n = n // 2
    return result
# Point addition
def point_add(P, Q, a, p):
    if P is None:
        return Q
    if Q is None:
        return P

    x1, y1 = P
    x2, y2 = Q

    if P == Q:
        m = (3 * x1**2 + a) * mulinv(2 * y1, p) % p
    else:
        m = (y2 - y1) * mulinv(x2 - x1, p) % p

    x3 = (m**2 - x1 - x2) % p
    y3 = (m * (x3-x1) + y1) % p

    return x3, y3
