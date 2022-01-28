package com.ringpublishing.tracking

enum class TrackingIdentifierError
{

	// There was networking problem during tracking identifier request
	REQUEST_ERROR,

	// Backend had problem to process tracking identifier request and returned unexpected response
	RESPONSE_ERROR,

	// Problem with connection was occurred
	CONNECTION_ERROR
}
