import secrets
import hashlib


def safer_token_and_hash(password, salt):
    token = secrets.token_urlsafe(32)
    digest = hashlib.pbkdf2_hmac("sha256", password.encode(), salt, 200000)
    return token, digest
