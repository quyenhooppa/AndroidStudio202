package com.example.calculator;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    TextView txtConsole;
    Button btn0;
    Button btn1;
    Button btn2;
    Button btn3;
    Button btn4;
    Button btn5;
    Button btn6;
    Button btn7;
    Button btn8;
    Button btn9;
    Button btnDot;
    Button btnAC;
    Button btnDEL;
    Button btnAdd;
    Button btnSub;
    Button btnMul;
    Button btnDiv;
    Button btnEq;
    double result = 0;
    char operation = '\n';
    boolean click = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        setContentView(R.layout.activity_main);

        txtConsole = findViewById(R.id.result);

        //------------------------- ADD OPERATION -------------------------//
        btnAdd = findViewById(R.id.btnAdd);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                click = false;
                String data = txtConsole.getText().toString();
                double myNum = 0;
                try {
                    myNum = Double.parseDouble(data);
                } catch(NumberFormatException nfe) {
                    txtConsole.setText("0");
                }
                if (operation == '\n')
                    result = myNum;
                else {
                    if (operation == '+')
                        result += myNum;
                    else if (operation == '-')
                        result -= myNum;
                    else if (operation == '*')
                        result *= myNum;
                    else if (operation == '/')
                        result /= myNum;
                }
                operation = '+';
                data = "";
                data = data.valueOf(result);
                if (data.substring(data.length() - 2).equals(".0"))
                    data = data.substring(0, data.length() - 2);
                txtConsole.setText(data);
            }
        });

        //------------------------- SUBTRACT OPERATION -------------------------//
        btnSub = findViewById(R.id.btnSub);
        btnSub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                click = false;
                String data = txtConsole.getText().toString();
                double myNum = 0;
                try {
                    myNum = Double.parseDouble(data);
                } catch(NumberFormatException nfe) {
                    txtConsole.setText("0");
                }
                if (operation == '\n')
                    result = myNum;
                else {
                    if (operation == '+')
                        result += myNum;
                    else if (operation == '-')
                        result -= myNum;
                    else if (operation == '*')
                        result *= myNum;
                    else if (operation == '/')
                        result /= myNum;
                }
                operation = '-';
                data = "";
                data = data.valueOf(result);
                if (data.substring(data.length() - 2).equals(".0"))
                    data = data.substring(0, data.length() - 2);
                txtConsole.setText(data);
            }
        });

        //------------------------- MULTIPLICATION OPERATION -------------------------//
        btnMul = findViewById(R.id.btnMul);
        btnMul.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                click = false;
                String data = txtConsole.getText().toString();
                double myNum = 0;
                try {
                    myNum = Double.parseDouble(data);
                } catch(NumberFormatException nfe) {
                    txtConsole.setText("0");
                }
                if (operation == '\n')
                    result = myNum;
                else {
                    if (operation == '+')
                        result += myNum;
                    else if (operation == '-')
                        result -= myNum;
                    else if (operation == '*')
                        result *= myNum;
                    else if (operation == '/')
                        result /= myNum;
                }
                operation = '*';
                data = "";
                data = data.valueOf(result);
                if (data.substring(data.length() - 2).equals(".0"))
                    data = data.substring(0, data.length() - 2);
                txtConsole.setText(data);
            }
        });

        //------------------------- DIVISION OPERATION -------------------------//
        btnDiv = findViewById(R.id.btnDiv);
        btnDiv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                click = false;
                String data = txtConsole.getText().toString();
                double myNum = 0;
                try {
                    myNum = Double.parseDouble(data);
                } catch(NumberFormatException nfe) {
                    txtConsole.setText("0");
                }
                if (operation == '\n')
                    result = myNum;
                else {
                    if (operation == '+')
                        result += myNum;
                    else if (operation == '-')
                        result -= myNum;
                    else if (operation == '*')
                        result *= myNum;
                    else if (operation == '/')
                        result /= myNum;
                }
                operation = '/';
                data = "";
                data = data.valueOf(result);
                if (data.substring(data.length() - 2).equals(".0"))
                    data = data.substring(0, data.length() - 2);
                txtConsole.setText(data);
            }
        });


        //------------------------- PERCENT OPERATION -------------------------//
        btnDiv = findViewById(R.id.btnPercent);
        btnDiv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                click = false;
                String data = txtConsole.getText().toString();
                double myNum = 0;
                try {
                    myNum = Double.parseDouble(data);
                } catch(NumberFormatException nfe) {
                    txtConsole.setText("0");
                }
                result = myNum / 100;
                data = "";
                data = data.valueOf(result);
                if (data.substring(data.length() - 2).equals(".0"))
                    data = data.substring(0, data.length() - 2);
                txtConsole.setText(data);
            }
        });

        //------------------------- EQUAL OPERATION -------------------------//
        btnEq = findViewById(R.id.btnEqual);
        btnEq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                click = false;
                String data = txtConsole.getText().toString();
                double myNum = 0;
                try {
                    myNum = Double.parseDouble(data);
                } catch(NumberFormatException nfe) {
                    txtConsole.setText("0");
                }
                if (operation == '\n')
                    result = myNum;
                else {
                    if (operation == '+')
                        result += myNum;
                    else if (operation == '-')
                        result -= myNum;
                    else if (operation == '*')
                        result *= myNum;
                    else if (operation == '/')
                        result /= myNum;
                    else if (operation == '%')
                        result /= 100;
                }
                operation = '\n';
                data = "";
                data = data.valueOf(result);
                if (data.substring(data.length() - 2).equals(".0"))
                    data = data.substring(0, data.length() - 2);
                txtConsole.setText(data);
            }
        });


        //------------------------- ALL CLEAR BUTTON -------------------------//
        btnAC = findViewById(R.id.btnAC);
        btnAC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txtConsole.setText("0");
                result = 0;
                click = true;
                operation = '\n';
            }
        });


        //------------------------- DELETE BUTTON -------------------------//
        btnDEL = findViewById(R.id.btnDEL);
        btnDEL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String data = txtConsole.getText().toString();
                if (data.length() > 1)
                    if (data.contains("E"))
                        data = "0";
                    else
                        data = data.substring(0, data.length() - 1);
                else
                    data = "0";
                txtConsole.setText(data);
                click = true;
            }
        });


        //------------------------- NUMBER BUTTONS -------------------------//
        btn0 = findViewById(R.id.btn0);
        btn0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String data = txtConsole.getText().toString();
                if (data.equals("0"))
                    data = "";
                data += '0';
                if (click == false) {
                    txtConsole.setText("0");
                    click = true;
                }
                else
                txtConsole.setText(data);
            }
        });

        btn1 = findViewById(R.id.btn1);
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String data = txtConsole.getText().toString();
                if (data.equals("0"))
                    data = "";
                data += '1';
                if (click == false) {
                    txtConsole.setText("1");
                    click = true;
                }
                else
                    txtConsole.setText(data);
            }
        });

        btn2 = findViewById(R.id.btn2);
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String data = txtConsole.getText().toString();
                if (data.equals("0"))
                    data = "";
                data += '2';
                if (click == false) {
                    txtConsole.setText("2");
                    click = true;
                }
                else
                    txtConsole.setText(data);
            }
        });

        btn3 = findViewById(R.id.btn3);
        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String data = txtConsole.getText().toString();
                if (data.equals("0"))
                    data = "";
                data += '3';
                if (click == false) {
                    txtConsole.setText("3");
                    click = true;
                }
                else
                    txtConsole.setText(data);
            }
        });

        btn4 = findViewById(R.id.btn4);
        btn4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String data = txtConsole.getText().toString();
                if (data.equals("0"))
                    data = "";
                data += '4';
                if (click == false) {
                    txtConsole.setText("4");
                    click = true;
                }
                else
                    txtConsole.setText(data);
            }
        });

        btn5 = findViewById(R.id.btn5);
        btn5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String data = txtConsole.getText().toString();
                if (data.equals("0"))
                    data = "";
                data += '5';
                if (click == false) {
                    txtConsole.setText("5");
                    click = true;
                }
                else
                    txtConsole.setText(data);
            }
        });

        btn6 = findViewById(R.id.btn6);
        btn6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String data = txtConsole.getText().toString();
                if (data.equals("0"))
                    data = "";
                data += '6';
                if (click == false) {
                    txtConsole.setText("6");
                    click = true;
                }
                else
                    txtConsole.setText(data);
            }
        });

        btn7 = findViewById(R.id.btn7);
        btn7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String data = txtConsole.getText().toString();
                if (data.equals("0"))
                    data = "";
                data += '7';
                if (click == false) {
                    txtConsole.setText("7");
                    click = true;
                }
                else
                    txtConsole.setText(data);
            }
        });

        btn8 = findViewById(R.id.btn8);
        btn8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String data = txtConsole.getText().toString();
                if (data.equals("0"))
                    data = "";
                data += '8';
                if (click == false) {
                    txtConsole.setText("8");
                    click = true;
                }
                else
                    txtConsole.setText(data);
            }
        });

        btn9 = findViewById(R.id.btn9);
        btn9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String data = txtConsole.getText().toString();
                if (data.equals("0"))
                    data = "";
                data += '9';
                if (click == false) {
                    txtConsole.setText("9");
                    click = true;
                }
                else
                    txtConsole.setText(data);
            }
        });

        btnDot = findViewById(R.id.btnDecimal);
        btnDot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String data = txtConsole.getText().toString();
                if (data.contains(".") == false)
                    data += '.';
                txtConsole.setText(data);
            }
        });
    }
}