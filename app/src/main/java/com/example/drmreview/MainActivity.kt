package com.example.drmreview

import android.media.MediaDrm
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.util.UUID

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //make the array list of DRM system UUID
        val candidateDrmSystemUuidArray = resources.getStringArray(R.array.uuid_list)
        val targetDrmSystemArrayList = identifySupportedDrmSystems(candidateDrmSystemUuidArray)

        //make the array list of MIME content types
        val candidateMimeArray = resources.getStringArray(R.array.mime_list)
        val targetMimeArrayList = identifySupportedMime(targetDrmSystemArrayList, candidateMimeArray)

        //make the array list of DRM properties
        val drmPropertyArrayList = retrieveDrmProperties(targetDrmSystemArrayList, targetMimeArrayList)

        //output the DRM report
        val recyclerView = findViewById<RecyclerView>(R.id.main_recycler)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(this).apply {
            orientation = LinearLayoutManager.VERTICAL
        }

        recyclerView.adapter = MyListAdapter(drmPropertyArrayList)
    }

    //return array list of workable UUID
    private fun identifySupportedDrmSystems(uuidList: Array<String>) : ArrayList<UUID>{
        val uuidArrayList: ArrayList<UUID> = arrayListOf()

        for (strUuid in uuidList){
            val uuid = UUID.fromString(strUuid)

            if (MediaDrm.isCryptoSchemeSupported(uuid)){
                uuidArrayList.add(uuid)
            }
        }

        return uuidArrayList
    }

    //return array list of supported MIME type
    private fun identifySupportedMime(uuidList: ArrayList<UUID>, mimeList: Array<String>) : ArrayList<String>{
        val mimeArrayList: ArrayList<String> = arrayListOf()

        for (uuid in uuidList){
            for (strMime in mimeList){
                if (MediaDrm.isCryptoSchemeSupported(uuid, strMime)){
                    mimeArrayList.add(strMime)
                }
            }
        }

        return mimeArrayList
    }

    //return array list of DRM properties
    private fun retrieveDrmProperties(uuidList: ArrayList<UUID>, mimeList: ArrayList<String>) : ArrayList<ListItem>{
        val mediaDrmProperties = listOf(
            MediaDrm.PROPERTY_DESCRIPTION,
            MediaDrm.PROPERTY_VERSION,
            MediaDrm.PROPERTY_VENDOR,
            MediaDrm.PROPERTY_ALGORITHMS,
            "securityLevel"
            //"hdcpLevel"
        )

        val drmIndexList = resources.getStringArray(R.array.drm_index_list)
        val drmDescriptionList = resources.getStringArray(R.array.drm_description_list)

        val drmPropertyArrayList: ArrayList<ListItem> = arrayListOf()

        for (uuid in uuidList){
            var i = 0

            mediaDrmProperties.forEach {
                drmPropertyArrayList.add(
                    ListItem(
                        drmIndexList[i],
                        MediaDrm(uuid).getPropertyString(it),
                        drmDescriptionList[i]
                    )
                )
                i++
            }

            //check HDCP level
            drmPropertyArrayList.add(
                ListItem(
                    drmIndexList[i],
                    MediaDrm(uuid).maxHdcpLevel.toString() + " / " + MediaDrm(uuid).connectedHdcpLevel.toString(),
                    drmDescriptionList[i]
                )
            )

            //check security level at MIME content level
            retrieveSecurityProperties(uuid, mimeList, drmPropertyArrayList)
        }

        return drmPropertyArrayList
    }

    private fun retrieveSecurityProperties(uuid: UUID, mimeList: ArrayList<String>, drmPropertyArrayList: ArrayList<ListItem>){
        val securityProperties = listOf(
            MediaDrm.SECURITY_LEVEL_UNKNOWN,
            MediaDrm.SECURITY_LEVEL_SW_SECURE_CRYPTO,
            MediaDrm.SECURITY_LEVEL_SW_SECURE_DECODE,
            MediaDrm.SECURITY_LEVEL_HW_SECURE_CRYPTO,
            MediaDrm.SECURITY_LEVEL_HW_SECURE_DECODE,
            MediaDrm.SECURITY_LEVEL_HW_SECURE_ALL
        )

        val securityIndexList = resources.getStringArray(R.array.security_index_list)
        val securityDescriptionList = resources.getStringArray((R.array.security_description_list))

        for (mime in mimeList){
            var i = 0

            securityProperties.forEach {
                drmPropertyArrayList.add(
                    ListItem(
                        mime + " : " + securityIndexList[i],
                        MediaDrm.isCryptoSchemeSupported(uuid, mime, it).toString(),
                        securityDescriptionList[i]
                    )
                )
                i++
            }
        }
    }
}