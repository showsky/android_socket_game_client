package com.miiitv.game.client.gui;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;

import com.miiitv.game.client.R;

public class Client extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
