package com.cellmesh.nearby.nearbyapp

import android.support.v4.content.ContextCompat
import android.util.Log
import io.underdark.Underdark
import io.underdark.transport.Link
import io.underdark.transport.Transport
import io.underdark.transport.TransportKind
import io.underdark.transport.TransportKind.*
import io.underdark.transport.TransportListener
import java.util.*
import kotlin.collections.ArrayList
import io.underdark.util.nslogger.NSLogger
import io.underdark.util.nslogger.NSLoggerAdapter




/**
 * Created by Benjamin on 11/4/2017.
 */
class Node(
        val activity: MainActivity,
        val transport_types: EnumSet<TransportKind>,
        val nodeId: Long) : TransportListener {

    val links = mutableListOf<Link>()
    val transport = Underdark.configureTransport(
            234235,
            nodeId,
            this,
            null,
            activity.getApplicationContext(),
            transport_types)

    fun start(){
        val adapter = StaticLoggerBinder.getSingleton().getLoggerFactory().getLogger(Node::class.java.name) as NSLoggerAdapter
        adapter.logger = NSLogger(activity.applicationContext)
        adapter.logger.connect("192.168.5.203", 50000)
        Underdark.configureLogging(true)
        Log.d("Mesh","Starting transport")
        transport.start()
    }


    fun stop(){
        Log.d("Mesh","Stopping Transport")
        transport.stop()
    }
    fun broadcastBytes(data:ByteArray){
        Log.d("Mesh","Sending ${data.size} Bytes")
        for(link in links){
            link.sendFrame(data)
        }
    }
    fun sendBytes(data:ByteArray,nodeId: Long){
        links.
                firstOrNull { link->link.nodeId==nodeId }
                ?.sendFrame(data)
    }


    override fun transportNeedsActivity(transport: Transport?, callback: TransportListener.ActivityCallback?) {
        callback?.let { it.accept(activity) }
    }

    override fun transportLinkDidReceiveFrame(transport: Transport?, link: Link?, data: ByteArray?) {
        Log.d("Mesh","Got ${data?.let{it.size}} bytes from ${link?.let { it.nodeId }}")
    }

    override fun transportLinkConnected(transport: Transport?, link: Link?) {
        Log.d("Mesh","${link?.let{it.nodeId}} Connected")
        link?.let { links.add(it) }

        activity.handle_swarm_update(links.map { it.nodeId }.toList())
    }

    override fun transportLinkDisconnected(transport: Transport?, link: Link?) {
        Log.d("Mesh","${link?.let{it.nodeId}} Connected")
        link?.let { links.remove(it) }
        activity.handle_swarm_update(links.map { it.nodeId }.toList())
    }


}