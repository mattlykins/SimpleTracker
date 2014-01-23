package com.mattlykins.simpletracker;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Date;
import java.util.Stack;


import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

import com.mattlykins.database.*;
import com.mattlykins.datetime.*;

public class MainActivity extends Activity {

    GridView gvKeypad;
    TextView tvInput;
    
    SimpleTrackerDbAdapter mDbHelper;

    Stack<String> stackInput;

    KeypadAdapter kaKeypad;

    String decimalSeparator; 
    
    // Identifies whether a mathematical operation has been added to the stack
    boolean operatorSet = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        mDbHelper = new SimpleTrackerDbAdapter(this);
        mDbHelper.open();
        DecimalFormat currencyFormatter = (DecimalFormat) NumberFormat.getInstance();
        char decimalSeperator = currencyFormatter.getDecimalFormatSymbols().getDecimalSeparator();
        decimalSeparator = Character.toString(decimalSeperator);

        // Create the stack
        stackInput = new Stack<String>();

        
        gvKeypad = (GridView) findViewById(R.id.grdButtons);        
        tvInput = (TextView) findViewById(R.id.txtInput);
        tvInput.setText("0");

        
        kaKeypad = new KeypadAdapter(this);       
        gvKeypad.setAdapter(kaKeypad);

        // Set button click listener of the keypad adapter
        kaKeypad.setOnButtonClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Button btn = (Button) v;
                // Get the KeypadButton value which is used to identify the
                // keypad button from the Button's tag
                KeypadButton keypadButton = (KeypadButton) btn.getTag();

                // Process keypad button
                ProcessKeypadInput(keypadButton);
            }
        });

        gvKeypad.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {

            }
        });

    }
    
    //Handle all of the button presses on the grid view keypad
    private void ProcessKeypadInput(KeypadButton keypadButton) {
        //Toast.makeText(this, keypadButton.getText(),Toast.LENGTH_SHORT).show();
        
        String keyText = keypadButton.getText().toString();
        String currentInput = tvInput.getText().toString();        

        int currentInputLen = currentInput.length();

        switch (keypadButton) {
            case STORE:
                DateString ds = new DateString();                
                Date currentDate = new Date();
                String parsedDate = ds.dateToString(currentDate);
                
                mDbHelper.saveEntry(currentInput, parsedDate);
                
                break;
            case BACKSPACE:

                int endIndex = currentInputLen - 1;

                // There is one character at input so reset input to 0
                if (endIndex < 1) {
                    tvInput.setText("0");
                }
                // Trim last character of the input text
                else {
                    tvInput.setText(currentInput.subSequence(0, endIndex));
                }
                break;
//            case CE: // Handle clear input
//                tvInput.setText("0");
//                break;
            case C: // Handle clear input and stack
                tvInput.setText("0");
                stackInput.clear();
                operatorSet = false;
                break;
            case DECIMAL_SEP: // Handle decimal seperator
                if (tvInput.equals("0")) {
                    tvInput.setText("0" + decimalSeparator);
                }
                else if (countOccurrences(currentInput,decimalSeparator.charAt(0)) > 1)
                {
                    return;
                }
                else
                    tvInput.append(decimalSeparator);
                break;
            case DIV:
            case PLUS:
            case MINUS:
            case MULTIPLY:
                if(!currentInput.equals("0") && !operatorSet)
                {                
                    tvInput.append(keyText);
                    stackInput.add(currentInput);
                    stackInput.add(keyText);
                }
                operatorSet = true;
                break;
            case CALCULATE:
                if( stackInput.size() == 2 ){
                    String stackString = stackInput.get(0)+stackInput.get(1);
                    String secondInput = tvInput.getText().subSequence(stackString.length(), tvInput.length()).toString();
                    
                    
                    if( countOccurrences(stackString,decimalSeparator.charAt(0)) < 2 &&
                            countOccurrences(secondInput,decimalSeparator.charAt(0)) < 2 ){
                        
                        stackInput.add(secondInput);                
                        //Toasty(String.valueOf(stackInput.size()));
                        EvaluateStack();
                        emptyStack();
                    }
                }
                break;
            default:
                if (Character.isDigit(keyText.charAt(0))) {
                    if (currentInput.equals("0")){
                        tvInput.setText(keyText);
                    }
                    else{
                        tvInput.append(keyText);
                    }
                }
                break;
        }
    }
    
    // Display a toast for quick debugging
    @SuppressWarnings("unused")
    private void Toasty(String say){        
        Toast.makeText(this, say, Toast.LENGTH_SHORT).show();
    }
    
    // Evaluate the single operation that is stored in the stack
    private void EvaluateStack(){
        if( operatorSet && stackInput.size() == 3 ){
            double firstNum = Double.valueOf(stackInput.get(0));
            String operation = stackInput.get(1);
            double secondNum = Double.valueOf(stackInput.get(2));
            
            double result = Double.NaN;            
            
            if(operation.equals(KeypadButton.DIV.getText())){
                result = firstNum/secondNum;                                
            }
            else if( operation.equals(KeypadButton.MULTIPLY.getText())){
                result = firstNum*secondNum;
            }
            else if( operation.equals(KeypadButton.PLUS.getText())){
                result = firstNum+secondNum;
            }
            else if( operation.equals(KeypadButton.MINUS.getText())){
                result = firstNum-secondNum;                
            }
            
            tvInput.setText(doubleOrLong(result));
            operatorSet = false;            
        }
        
    }
    
    
    //Clear operation stack. Should be called after calculating and on a clear
    private void emptyStack(){
        stackInput.clear();
    }
    
    //Count the number of times a character appears within a string
    private int countOccurrences(String text,char character){
        int occurr = 0;
        
        for(int i = 0; i < text.length(); i++){
            if( text.charAt(i) == character ){
                occurr++;
            }
        }        
        return occurr;
    }
    
    // Return the String of a Long if possible, otherwise the String of a double
    private String doubleOrLong(double value){
        if (Double.isNaN(value) || Double.isInfinite(value))
            return null;
        
        long longValue = (long)value;
        if (value == longValue){
            return Long.toString(longValue);
        }
        else{
            //This will round to the nearest 0.1
            return String.format("%.1f",value);
        }
    }
}
