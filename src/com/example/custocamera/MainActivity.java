package com.example.custocamera;

import com.androidquery.AQuery;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.method.DigitsKeyListener;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

public class MainActivity extends AppCompatActivity {

	
	public AQuery aq;
	public EditText ed1;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		aq= new AQuery(this);
		
		aq.id(R.id.button1).clicked(aos);
		aq.id(R.id.button2).clicked(aos);
		aq.id(R.id.button3).clicked(aos);
		aq.id(R.id.button4).clicked(aos);
		aq.id(R.id.button5).clicked(aos);
		aq.id(R.id.button6).clicked(aos);
		ed1 = (EditText)findViewById(R.id.ed1);
		
		//http://blog.csdn.net/lhbtm/article/details/55505668
    	//https://github.com/xingda920813/HelloCamera2
		//http://blog.csdn.net/xhy61/article/details/62427553
		
		//将输入法切换到英文
		//ed1.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
		//将输入法弹出的右下角的按钮改为完成，不改的话会是下一步。
		ed1.setImeOptions(EditorInfo.IME_ACTION_DONE);
		ed1.setFilters(new InputFilter[]{new InputFilter.LengthFilter(10)});
		ed1.addTextChangedListener(new TextWatcher() {
			
			private CharSequence temp;
			//文本变化之前
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				Log.i("文本变化之前", ""+s);
				temp = s;
				
			}
			//文本变化中
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				Log.i("文本变化中", ""+s);
				
			}
			//文本变化之后
			@Override
			public void afterTextChanged(Editable s) {
				Log.i("文本变化之后", ""+s);
				if (temp.length()>=1) {
					Log.i("變化", "變化 數字");
					ed1.setInputType(InputType.TYPE_CLASS_PHONE);
				}
				else
				{
					Log.i("變化", "變化 英文");
					ed1.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
				}
			}
		});
	}

	public OnClickListener aos = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.button1:
				Intent intent = new Intent(MainActivity.this, Camera2Page1.class);
				startActivity(intent);
				break;
			case R.id.button2:
				Intent intent2 = new Intent(MainActivity.this, Camera2Page2.class);
				startActivity(intent2);
				break;
			case R.id.button3:
				Intent intent3 = new Intent(MainActivity.this, Camera1Page1.class);
				startActivity(intent3);
				break;
			case R.id.button4:
				Intent intent4 = new Intent(MainActivity.this, Camera1Page2.class);
				startActivity(intent4);
				break;
			case R.id.button5:
				Intent intent5 = new Intent(MainActivity.this, Camera1Page3.class);
				startActivity(intent5);
				break;
			case R.id.button6:
				Intent intent6 = new Intent(MainActivity.this, Camera2Page3.class);
				startActivity(intent6);
				break;
			}
		}
	};
}
