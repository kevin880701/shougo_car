package com.clockworkorange.repository.remote.http

import com.clockworkorange.repository.model.GenericErrorResponse

class ApiException(val response: GenericErrorResponse): Exception(response.errorMsg)