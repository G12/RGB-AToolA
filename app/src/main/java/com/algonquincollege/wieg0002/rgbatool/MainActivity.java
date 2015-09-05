/**
 * View and controller (i.e. a UI delegate) of a RGBA model.
 *
 * As the controller:
 *   a) event handler for the view
 *   b) observer of the model, which is an RGBAModel.
 *
 * Features the Update / React Strategy.
 *
 * @author Thomas Wiegand (wieg0002)
 * @version 1.0
 *
 */

package com.algonquincollege.wieg0002.rgbatool;

import java.util.Observable;
import java.util.Observer;

import model.HexBuilder;
import model.RGBAModel;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.NumberPicker.OnValueChangeListener;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

public class MainActivity extends Activity implements Observer, OnValueChangeListener {
	
	private static final String TAG;
	
	//Make a nice Cadmium Yellow Default
	private static int GREEN_DEFAULT;
	private static int BLUE_DEFAULT;
	// Static initializer block. Initialize all class variables here.
	static {
		TAG = "RGB-A TOOL";
		GREEN_DEFAULT = 178;
		BLUE_DEFAULT = 85;
	}

	NumberPicker red;
	NumberPicker green;
	NumberPicker blue;
	//I set my color and alpha view to a TextView not to the ViewGroup so that
	//The controls would not disappear when the alpha channel goes to 0
	RGBAModel model;

	TextView _textViewCurrentColour;
	EditText _editTextColourText;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        SharedPreferences settings = getSharedPreferences(getResources().getString(R.string.app_name), Context.MODE_PRIVATE);
        
	     // instantiate a new RGB-A model
	     // and observer it
        model = new RGBAModel(
        		 settings.getInt(RGBAModel.RED,   RGBAModel.MAX_RGB),
        	        settings.getInt(RGBAModel.GREEN, GREEN_DEFAULT),
        	        settings.getInt(RGBAModel.BLUE,  BLUE_DEFAULT),
        	        settings.getInt(RGBAModel.ALPHA, RGBAModel.MAX_ALPHA) 
        	    );

        model.addObserver(this);

        red =  (NumberPicker) findViewById(R.id.pickerRed);
        red.setMaxValue(RGBAModel.MAX_RGB);
        red.setMinValue(RGBAModel.MIN_RGB);
        red.setValue(RGBAModel.MAX_RGB);
        red.setWrapSelectorWheel(true); 
        red.setOnValueChangedListener(this);
        
        green =  (NumberPicker) findViewById(R.id.pickerGreen);
        green.setMaxValue(RGBAModel.MAX_RGB);
        green.setMinValue(0);
        green.setValue(GREEN_DEFAULT);
        green.setWrapSelectorWheel(true); 
        green.setOnValueChangedListener(this);

        blue =  (NumberPicker) findViewById(R.id.pickerBlue);
        blue.setMaxValue(RGBAModel.MAX_RGB);
        blue.setMinValue(RGBAModel.MIN_ALPHA);
        blue.setValue(BLUE_DEFAULT);
        blue.setWrapSelectorWheel(true);
        blue.setOnValueChangedListener(this);
        
        SeekBar alpha = (SeekBar) findViewById(R.id.seekBarAlpha);
        alpha.setProgress(RGBAModel.MIN_ALPHA);
        alpha.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				//Update the model Alpha with the new slider value. When this
				//happens the model will call its updateObservers method.
				//This will cause the update method of all registered observers
				//to be fired. In our case the only register observer is the MainActivity
				//class.
				Log.i( TAG, "Setting the model's alpha value to: " + progress );
				model.setAlpha(progress);
			}
		});
        
        _textViewCurrentColour = (TextView) findViewById(R.id.textViewCurrentColour);
        _editTextColourText = (EditText) findViewById(R.id.editTextCurrentColour);

        alpha.setProgress(model.getAlpha());
        this.updateView();
    }
    
 // Remember the user's settings for RGB-A
    @Override
    protected void onStop() {
      SharedPreferences settings = getSharedPreferences( getResources().getString(R.string.app_name), Context.MODE_PRIVATE );
      SharedPreferences.Editor editor = settings.edit();

      editor.putInt( RGBAModel.RED,   model.getRed() );
      editor.putInt( RGBAModel.GREEN, model.getGreen() );
      editor.putInt( RGBAModel.BLUE,  model.getBlue() );
      editor.putInt( RGBAModel.ALPHA, model.getAlpha() );

      editor.commit();
      super.onStop();
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch(id)
        {
        	case R.id.action_about:
        		return true;
		    case R.id.action_black:
		    	model.asBlack();
		    	return true;
		    case R.id.action_blue:
		    	model.asBlue();
		    	return true;
		    case R.id.action_cyan:
		    	model.asCyan();
		    	return true;
		    case R.id.action_green:
		    	model.asGreen();
		    	return true;
		    case R.id.action_magenta:
		    	model.asMagenta();
		    	return true;
		    case R.id.action_red:
		    	model.asRed();
		    	return true;
		    case R.id.action_white:
		    	model.asWhite();
		    	return true;
		    case R.id.action_yellow:
		    	model.asYellow();
		    	return true;
        }
        return super.onOptionsItemSelected(item);
    }

	@Override
	public void update(Observable observable, Object data) {
		//One of the Observable objects that has this class registered
		//has called it's updateObservers method. In our case there is
		//only one Observable, our model class, so we do not have to
		//test for which Observable to react to.
		
		//Display the change.
		this.updateView();
	}

	@Override
	public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
		//Update the model color for the picker that just changed when this
		//happens the model will call its updateObservers method.
		//This will cause the update method of all registered observers
		//to be fired. In our case the only register observer is the MainActivity
		//class.
		int id = picker.getId();
		switch(id)
		{
			case R.id.pickerRed:
				Log.i( TAG, "Setting the model's red value to: " + newVal );
				model.setRed(newVal);
				break;
			case R.id.pickerGreen:
				Log.i( TAG, "Setting the model's green value to: " + newVal );
				model.setGreen(newVal);
				break;
			case R.id.pickerBlue:
				Log.i( TAG, "Setting the model's blue value to: " + newVal );
				model.setBlue(newVal);
				break;
		}
	}
	
    private void changeColour()
    {
		//Set the background color of the TextView
    	_textViewCurrentColour.setBackgroundColor(model.getColor());
		_textViewCurrentColour.setTextColor(model.getTextColor());
		String hex = "Alpha: " + (((float) model.getAlpha()) / 100) + " Hex: ";
		HexBuilder hb = new HexBuilder(model.getRed(), model.getGreen(), model.getBlue());
		hex += hb.getHex();
		//Change the transparency of the TextView background
		_textViewCurrentColour.setAlpha(((float) model.getAlpha()) / 100 );
		_editTextColourText.setText(hex);
    }

	private void updateView()
	{
		red.setValue(model.getRed());
		green.setValue(model.getGreen());
		blue.setValue(model.getBlue());
		changeColour();
	}

}
