package com.ballgame

import com.ballgame.StateManager.StateKey.HIGH_SCORE
import com.ballgame.StateManager.StateKey.LIVES
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonPrimitive
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.nio.file.Files.newBufferedReader
import java.nio.file.Paths.get
import java.util.Locale.getDefault

class StateManager {
	private val tempDir: String = "./"
	private val storeFilename: String = "ballgame.state.json"
	private var body: JsonObject? = null
	private val gson: Gson = Gson()

	enum class StateKey {
		HIGH_SCORE, LIVES
	}

	init {
		val stateFile = File(get(tempDir, storeFilename).toString())
		try {
			if (!stateFile.exists()) {
				body = jsonBody;
				stateFile.createNewFile()
				updateKeyWithPrimitive(HIGH_SCORE, JsonPrimitive(0))
				updateKeyWithPrimitive(LIVES, JsonPrimitive(1))
			}
			body = jsonBody
		} catch (e: IOException) {
			e.printStackTrace()
		}
	}

	fun updateKeyWithPrimitive(key: StateKey, value: Any) {
		try {
			val map = HashMap<String, Any>()
			val writer = FileWriter(get(tempDir, storeFilename).toString())
			if (body != null) body!!.entrySet().forEach { (key1, value1) -> map[key1] = value1 }
			map[key.toString().lowercase(getDefault())] = value
			Gson().toJson(map, writer)
			writer.close()
			if (body == null) body = jsonBody
		} catch (e: IOException) {
			e.printStackTrace()
		}
	}

	/*val highScore: Int = try {
		if (body == null) throw IOException()
		body!![HIGH_SCORE.toString().lowercase(getDefault())].asInt
	} catch (e: IOException) {
		e.printStackTrace()
		-1
	}*/
	private fun getKey(key: StateKey) = try {
		if (body == null) throw IOException()
		body!![key.toString().lowercase(getDefault())]
	} catch (e: IOException) {
		e.printStackTrace()
		null
	}

	val highScore: Int
		get() = getKey(HIGH_SCORE)!!.asInt
	val lives: Int
		get() = getKey(HIGH_SCORE)?.asInt ?: -1

	fun refreshJsonBody() = try {
		body = jsonBody
	} catch (e: IOException) {
		e.printStackTrace()
	}

	@get:Throws(IOException::class)
	private val jsonBody: JsonObject
		get() = gson.fromJson(
			newBufferedReader(get(tempDir, storeFilename)), JsonObject::class.java
		)
}
