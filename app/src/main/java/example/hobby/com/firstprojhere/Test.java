package example.hobby.com.firstprojhere;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;



public class Test extends Activity {
    static int prevX;
    static int prevY;
    static final String leftClickCode = "ltclk";
    static final String rightClickCode = "rtclk";
    static final String doubleClickCode = "dblclk";
    static final String scrollUpCode = "up";
    static final String scrollDownCode = "down";
    static String ip = "127.0.0.1";
    static LinearLayout lytTouch;
    static Button scanQR;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        prevX = 0;
        prevY = 0;
        mouseActivities();
       // keyboardActivities();

    }

    /*private void keyboardActivities() {
        final EditText txtBox = (EditText) findViewById(R.id.txtKeyBoard);
        txtBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.i("Mouse", start+" "+before+" "+count+" "+s);
                if(before>count){
                    new KeyBoard().execute("kyboard bkspc");
                } else new KeyBoard().execute("kyboard "+s.charAt(count-1));

            }

           @Override
            public void afterTextChanged(Editable s) {

            }
        });


        *//*txtBox.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                Log.i("Mouse", "" + event.getKeyCode());
                return false;
            }
        });*//*
    }
*/
    private void mouseActivities() {
        lytTouch = (LinearLayout) findViewById(R.id.lytTouch);
        final GestureDetector detector = new GestureDetector(Test.this, new GestureDetector.SimpleOnGestureListener() {

            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                if (e.getPointerCount() == 1)
                    new MouseClick().execute(leftClickCode);
                return super.onSingleTapConfirmed(e);
            }

            @Override
            public boolean onDoubleTap(MotionEvent e) {
                if (e.getPointerCount() == 1)
                    new MouseClick().execute(doubleClickCode);
                return super.onDoubleTap(e);
            }

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                if (e1.getPointerCount() > 1) {
                    if (distanceX < 0) {
                        new MouseClick().execute(scrollUpCode);
                    } else {
                        new MouseClick().execute(scrollDownCode);
                    }

                }
                return super.onScroll(e1, e2, distanceX, distanceY);
            }

            @Override
            public void onLongPress(MotionEvent e) {
                new MouseClick().execute(rightClickCode);
                super.onLongPress(e);
            }
        });
        lytTouch.setVisibility(View.GONE);
        lytTouch.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // Log.i("Mouse", String.valueOf(Math.round(event.getX())) + "x" + String.valueOf(Math.round(event.getY())));
                if (detector.onTouchEvent(event)) {
                    return true;
                }
                if (event.getAction() == android.view.MotionEvent.ACTION_MOVE) {
                    float x = event.getX();
                    float y = event.getY();
                    String[] coordinates = {String.valueOf(Math.round(x - prevX)), String.valueOf(Math.round(y - prevY))};
                    prevX = Math.round(x);
                    prevY = Math.round(y);
                    new MouseMove().execute(coordinates);
                } else if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    prevX = Math.round(event.getX());
                    prevY = Math.round(event.getY());
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    prevX = Math.round(event.getX());
                    prevY = Math.round(event.getY());
                }
                return true;
            }
        });

        scanQR = (Button) findViewById(R.id.btnScan);
        scanQR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent("com.google.zxing.client.android.SCAN");
                intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
                startActivityForResult(intent, 0);
            }
        });
    }
        public void onActivityResult(int requestCode, int resultCode, Intent intent) {
            Log.i("Mouse", "Entered");
            if (requestCode == 0) {
                if (resultCode == RESULT_OK) {
                    String contents = intent.getStringExtra("SCAN_RESULT");
                    if(contents!=null){
                        ip = contents;
                        Toast.makeText(Test.this, "Successfully connected!", Toast.LENGTH_LONG).show();
                        lytTouch.setVisibility(View.VISIBLE);
                        scanQR.setVisibility(View.GONE);

                    }


                } else if (resultCode == RESULT_CANCELED) {
                    // Handle cancel
                    Toast toast = Toast.makeText(this, "Scan was Cancelled!", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.TOP, 25, 400);
                    toast.show();

                }
            }
        }





    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_test, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}

class MouseMove extends AsyncTask {

    @Override
    protected Object doInBackground(Object[] params) {

        try {

            Socket socket = new Socket(Test.ip, 7777);
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             Log.i("Mouse", params[0] + "," + params[1]);
            out.println(params[0] + "," + params[1]);

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}

class MouseClick extends AsyncTask {
    @Override
    protected Object doInBackground(Object[] params) {
        String ip = "192.168.0.12";
        try {
            Socket socket = new Socket(Test.ip, 7777);
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            out.println(params[0]);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

}

class KeyBoard extends AsyncTask {
    @Override
    protected Object doInBackground(Object[] params) {
        String ip = "192.168.0.12";
        try {
            Socket socket = new Socket(Test.ip, 7777);
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            out.println(params[0]);

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}

