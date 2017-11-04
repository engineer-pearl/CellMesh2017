package com.cellmesh.nearby.nearbyapp

import android.Manifest
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v4.content.PermissionChecker.PERMISSION_GRANTED
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import io.underdark.transport.TransportKind

import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity() {
    val chaos = Random()
    fun getNodeId(): Long {
        var id: Long = 0;
        while (id.equals(0)) {
            id = chaos.nextLong()
        }
        return id
    }

    var node: Node? = null
    override fun onCreate(savedInstanceState: Bundle?) {


        super.onCreate(savedInstanceState)

        check_permissions()


        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        node = Node(
                this,
                EnumSet.of<TransportKind>(TransportKind.WIFI),
                getNodeId())

        node?.let {
            it.start()
        }
        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }

    }

    fun handle_swarm_update(nodes: List<Long>) {
        val bldr = StringBuilder()
        for (node in nodes) {
            bldr.appendln(node)
        }
        val view: TextView = this.findViewById<TextView>(R.id.Status)
        view.setText(bldr.toString())
    }

    fun handle_connected(nodes: List<Long>) {

    }

    fun updateNodeCount() {

    }

    fun check_permissions() {
        val permissions = listOf<String>(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.ACCESS_WIFI_STATE,
                Manifest.permission.CHANGE_WIFI_STATE,
                Manifest.permission.CHANGE_NETWORK_STATE)

        permissions.forEach { needed: String ->
            if (ContextCompat.checkSelfPermission(this, needed) != PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        arrayOf<String>(needed),
                        1)
            }
        }


    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onDestroy() {
        node?.let { it.stop() }
        super.onDestroy()

    }
}
