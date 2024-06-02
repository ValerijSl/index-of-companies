package cz.uun.index.response

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus


@ResponseStatus(value = HttpStatus.NOT_FOUND)
internal class ResourceNotFoundException : RuntimeException()