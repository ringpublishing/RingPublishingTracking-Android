package com.ringpublishing.tracking.internal.api

import com.google.gson.GsonBuilder
import com.ringpublishing.tracking.internal.api.data.IdsMap
import com.ringpublishing.tracking.internal.api.request.ArtemisIdRequest
import com.ringpublishing.tracking.internal.api.request.EventRequest
import com.ringpublishing.tracking.internal.api.request.IdentifyRequest
import com.ringpublishing.tracking.internal.api.serialization.IdsMapDeserializer
import com.ringpublishing.tracking.internal.network.NetworkClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.net.URL

internal class ApiClient(private val apiKey: String, private val apiUrl: URL, private val networkClient: NetworkClient)
{

	private val api: ApiDefinition by lazy { buildRetrofit().create(ApiDefinition::class.java) }

	suspend fun identify(identifyRequest: IdentifyRequest) = api.identify(apiKey, identifyRequest)

    suspend fun getArtemisId(artemisIdRequest: ArtemisIdRequest) = api.getArtemisId(apiKey, artemisIdRequest)

    suspend fun send(eventRequest: EventRequest) = api.send(apiKey, eventRequest)

	private fun buildRetrofit() = Retrofit.Builder()
		.baseUrl(apiUrl)
		.addConverterFactory(
			GsonConverterFactory.create(
				GsonBuilder()
					.registerTypeAdapter(IdsMap::class.java, IdsMapDeserializer())
					.create()
			)
		)
		.client(networkClient.get())
		.build()
}
