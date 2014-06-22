package com.example.myapplication5.app.NFCWriter.ActionFolder;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.Display;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.myapplication5.app.R;
import com.example.myapplication5.app.ReceivingData;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import BussinessLogic.Common.DataHelper;
import Models.SettingsModel;

public class TextActivity extends Activity{

    String[] ClassesList = new String[] { "Text","Email", "Message", "Phone number", "Url"};
    EditText target;
    EditText subject;
    EditText message;
    Button fromContacts;
    Button writeTag;
    DataHelper _dataHelper;
    String mode;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nfc_tag_writer_text_activity);

        toDefaultState();
        _dataHelper = new DataHelper();

            mode = getIntent().getStringExtra("Mode");
            FillInUI(mode);

        writeTag = (Button)findViewById(R.id.write);
        final String modeElement = mode;
        writeTag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                boolean matched = _dataHelper.isMatched(target.getText().toString(), modeElement);
                if (matched)
                {

                    SettingsModel model = new SettingsModel();
                    model.Target = target.getText().toString();
                    model.Choice = modeElement;
                    if (subject.getVisibility() == View.VISIBLE)
                    {
                        model.Subject = subject.getText().toString();
                    }
                    if (message.getVisibility() == View.VISIBLE)
                    {
                        model.Message = message.getText().toString();
                    }


                    Intent myIntent = new Intent(TextActivity.this, ReceivingData.class);
                    myIntent.putExtra("Activity","Writer");
                    myIntent.putExtra("SettingsModel",new Gson().toJson(model));

                    startActivity(myIntent);

                }

            }
        });
    }

    private void toDefaultState(){
        target = (EditText)findViewById(R.id.target);
        target.setVisibility(View.INVISIBLE);

        subject = (EditText)findViewById(R.id.subject);
        subject.setVisibility(View.INVISIBLE);

        message = (EditText)findViewById(R.id.message);
        message.setVisibility(View.INVISIBLE);

        fromContacts = (Button)findViewById(R.id.from_contacts);
        fromContacts.setVisibility(View.GONE);
    }

    private void FillInUI(String mode)
    {
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        _dataHelper = new DataHelper();
        switch (_dataHelper.getPositionInArray(ClassesList, mode))
        {
            case 0: {
                target.setVisibility(View.VISIBLE);
                target.setHint("Write " + mode);
                target.setMinHeight(150);
                target.setWidth(size.x -10);
                break;
            }
            case 1:{
                target.setVisibility(View.VISIBLE);
                target.setHint("Write " + mode);

                subject.setVisibility(View.VISIBLE);
                subject.setHint("Write subject");

                message.setVisibility(View.VISIBLE);
                message.setHint("Write Message");
                message.setMinHeight(150);

                fromContacts.setVisibility(View.VISIBLE);

                break;
            }
            case 2:{

                target.setVisibility(View.VISIBLE);
                target.setHint("Write Phone number");

                message.setVisibility(View.VISIBLE);
                message.setHint("Write Message");
                message.setMinHeight(150);

                fromContacts.setVisibility(View.VISIBLE);
                break;
            }
            case 3:{
                target.setVisibility(View.VISIBLE);
                target.setHint("Write " + mode);

                fromContacts.setVisibility(View.VISIBLE);
                break;
            }
            case 4:{
                target.setVisibility(View.VISIBLE);
                target.setHint("Write " + mode);
                target.setWidth(size.x -10);
                break;
            }
        }

        if (fromContacts.getVisibility() == View.VISIBLE)
        {
           fromContacts.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                   Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                   startActivityForResult(intent, 1);

               }
          });
        }
    }

    private String getEmail(String id) {
        Cursor cursor;
        String email = "";
        try {
            cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, null, ContactsContract.CommonDataKinds.Email.CONTACT_ID + "=?", new String[]{id}, null);

            int nameId = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);

            int emailIdx = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA);

            // let's just get the first email
            if (cursor.moveToFirst()) {
                email = cursor.getString(emailIdx);


            } else {
                return email;
            }

            cursor.close();
        } catch (Exception e) {

        }
            return email;
    }

    private String getPhoneNumber(String id ) {

        String[] projection = {ContactsContract.CommonDataKinds.Phone.NUMBER};

        Cursor cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=?", new String[]{id}, null);


        List<String> list = new ArrayList<String>();

        if (cursor.moveToFirst()) {
            for (int z = 0; z < cursor.getCount(); z++) {
                int column = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                String number = cursor.getString(column);
                list.add(number);
            }
        }
        if (list.size() == 1) {
            return list.get(0);
        }
        else if (list.size() > 1){
            return selectFromList(_dataHelper.ToArray(list));
        }
        return "";
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode,resultCode,data);
        if (resultCode == RESULT_OK) {
            Uri contactUri = data.getData();
            String id = contactUri.getLastPathSegment();

            getResultString( contactUri, id,mode);
        }
    }


    private void getResultString( Uri contactUri, String contactId, String mode)
    {

        String text = "";
        switch (_dataHelper.getPositionInArray(ClassesList, mode))
        {

            case 1:{
                text = getEmail(contactId);
                break;
            }
            case 2:{
                text = getPhoneNumber(contactId);
                break;
            }
            case 3:{
                text = getPhoneNumber(contactId);
                break;
            }


        }
        target.setText(text);
    }

    private String selectFromList(final String[] list)
    {
        final String[] text = {""};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setItems(list, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int itemIdx) {
                text[0] = list[itemIdx];
            }
        });
        return text[0];
    }
}
