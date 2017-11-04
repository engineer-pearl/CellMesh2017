package com.cellmesh.app;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.util.Map;
import java.util.Random;
import java.util.Set;

import com.cellmesh.app.model.INodeListener;
import com.cellmesh.app.model.nameManager;
import com.cellmesh.app.model.Node;

public class MainActivity extends AppCompatActivity implements INodeListener
{
	private TextView peersTextView;
	private TextView framesTextView;

	Node node;

	private nameManager nm;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		peersTextView = (TextView) findViewById(R.id.peersTextView);
		framesTextView = (TextView) findViewById(R.id.framesTextView);

		//UI Must gather a name and create a listener before calling node.start
		node = new Node(this,null,"");

		// nm = new nameManager(node.

	}

	@Override
	protected void onStart()
	{
		super.onStart();
		node.start();
	}

	@Override
	protected void onStop()
	{
		super.onStop();

		if(node != null)
			node.stop();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();

		//noinspection SimplifiableIfStatement
		if (id == R.id.action_settings)
		{
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	private static boolean started = false;


	@Override
	public void onNamesUpdated(Map<Long, String> names) {

	}

	@Override
	public void onConnected(Set<Long> readOnlyIds, Long newId) {

	}

	@Override
	public void onDisconnected(Set<Long> readOnlyIds, Long oldLinkId) {

	}

	@Override
	public void onDataReceived(String newMessage, Long fromLinkId) {
		/*
		00: reserved
		01: node hash received
		02: node data received
		10: chat message received
		 */

		String op = newMessage.substring(0, 2);
		String message = newMessage.substring(2);

		if ( newMessage.startsWith("01") ) {
			// Compare hash to my hash. If equal do nothing.
			if ( message != nm.getHash() ) {

			}
		} else if ( newMessage.startsWith("02") ) {
			// Add name {ID:Name}?
		} else if ( newMessage.startsWith("10") ) {
			// Handle message received
		}
	}

	@Override
	public void onDataSent(String newMessage, Long fromLinkId) {

	}

	@Override
	public void onEmergency(Long fromLinkId) {

	}
} // MainActivity
