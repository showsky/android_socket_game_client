package com.miiitv.game.client.gui;

import com.miiitv.game.client.R;
import com.miiitv.game.client.R.layout;
import com.miiitv.game.client.R.menu;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

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
