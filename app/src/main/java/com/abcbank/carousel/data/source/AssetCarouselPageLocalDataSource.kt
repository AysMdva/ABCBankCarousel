package com.abcbank.carousel.data.source

import android.content.Context
import com.abcbank.carousel.data.model.CarouselPageSeed
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject

class AssetCarouselPageLocalDataSource(
    private val context: Context,
    private val assetFileName: String = DEFAULT_ASSET_FILE_NAME
) : CarouselPageLocalDataSource {

    override suspend fun getPageSeeds(): List<CarouselPageSeed> = withContext(Dispatchers.IO) {
        val rawJson = context.assets.open(assetFileName).bufferedReader().use { reader ->
            reader.readText()
        }
        val jsonArray = JSONArray(rawJson)

        List(jsonArray.length()) { index ->
            jsonArray.getJSONObject(index).toPageSeed()
        }
    }

    private fun JSONObject.toPageSeed(): CarouselPageSeed {
        val tagsJson = getJSONArray(TAGS_KEY)
        return CarouselPageSeed(
            imageRes = getString(ICON_KEY).toImageRes(),
            itemCount = getInt(ITEM_COUNT_KEY),
            tags = List(tagsJson.length()) { index -> tagsJson.getString(index) }
        )
    }

    private fun String.toImageRes(): Int = when (this) {
        ICON_GALLERY -> android.R.drawable.ic_menu_gallery
        ICON_CAMERA -> android.R.drawable.ic_menu_camera
        ICON_COMPASS -> android.R.drawable.ic_menu_compass
        ICON_EDIT -> android.R.drawable.ic_menu_edit
        ICON_MAP -> android.R.drawable.ic_menu_mapmode
        else -> error("Unsupported icon key: $this")
    }

    private companion object {
        const val DEFAULT_ASSET_FILE_NAME = "carousel_pages.json"

        const val ICON_KEY = "icon"
        const val ITEM_COUNT_KEY = "itemCount"
        const val TAGS_KEY = "tags"

        const val ICON_GALLERY = "gallery"
        const val ICON_CAMERA = "camera"
        const val ICON_COMPASS = "compass"
        const val ICON_EDIT = "edit"
        const val ICON_MAP = "map"
    }
}
