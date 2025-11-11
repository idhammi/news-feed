package id.idham.newsfeed.core.data.exception

sealed class NewsException(message: String) : Exception(message)

class UnauthorizedException(message: String) : NewsException(message)

class RateLimitException(message: String) : NewsException(message)

class NotFoundException(message: String) : NewsException(message)

class NetworkException(message: String) : NewsException(message)

class GenericException(message: String) : NewsException(message)