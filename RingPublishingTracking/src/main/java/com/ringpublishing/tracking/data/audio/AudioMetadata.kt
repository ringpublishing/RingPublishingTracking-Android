package com.ringpublishing.tracking.data.audio

/**
 * Audio meta data holder
 * @param [contentId] - Content identifier in source system (CMS)
 * @param [contentTitle] - Content title in source system
 * @param [contentSeriesId] - Content series identifier
 * @param [contentSeriesTitle] -  Content series title
 * @param [mediaType] - Audio media type (tts / podcast / livestream etc.)
 * @param [audioDuration] - Audio duration (in seconds)
 * @param [audioStreamFormat] - Type of audio stream format loaded into player @see [AudioStreamFormat]
 * @param [isContentFragment] - Is audio a part of a bigger content piece
 * @param [audioContentCategory] - Audio content category in terms of paid access to it @see [AudioContentCategory]
 * @param [audioPlayerVersion] - Audio player version name, for example 1.2.0
 */
data class AudioMetadata(
    val contentId: String,
    val contentTitle: String,
    val contentSeriesId: String?,
    val contentSeriesTitle: String?,
    val mediaType: String,
    val audioDuration: Int?,
    val audioStreamFormat: AudioStreamFormat,
    val isContentFragment: Boolean,
    val audioContentCategory: AudioContentCategory,
    val audioPlayerVersion: String
)
