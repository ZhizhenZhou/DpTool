package com.example.dplink_tool;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.huawei.hms.hmsscankit.ScanUtil;
import com.huawei.hms.ml.scan.HmsScan;
import com.huawei.hms.ml.scan.HmsScanAnalyzerOptions;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {


    public static final int DEFINED_VIEW = 0x22;
    public static final int REQUEST_CODE_SCAN = 0x01;

    private EditText mEdittext;
    private Button mButton;
    private ImageView mScanQRButton;
    private String muri;


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mButton = (Button)findViewById(R.id.button);
        mEdittext = (EditText) findViewById(R.id.edit_text);
        mScanQRButton = (ImageView) findViewById(R.id.scan_QR_code);
        muri = "";

        int mode = this.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        if (mode == Configuration.UI_MODE_NIGHT_YES) {
            mScanQRButton.setImageResource(R.drawable.icon_qrcode_navi);
        } else {
            mScanQRButton.setImageResource(R.drawable.icon_qrcode);
        }

        mButton.setOnClickListener(this);
        mScanQRButton.setOnClickListener(this);


    }

    @Override
    public void onClick (View view) {
        int id = view.getId();

        muri = mEdittext.getText().toString();
        Uri uri = Uri.parse(muri);
        switch (id) {
            case R.id.button:
                try {
                    Log.d("IntentActivity", muri);
                    Intent intent_StartActivity = new Intent();
                    intent_StartActivity.setAction(Intent.ACTION_VIEW);
                    intent_StartActivity.setData(uri);
                    startActivity(intent_StartActivity);
                    mEdittext.setText("");
                } catch (Exception e) {
                    Toast.makeText(this, "链接错误，请输入有效链接", Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.scan_QR_code:
                //调用相机扫码
                try {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        this.requestPermissions(
                                new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE},
                                DEFINED_VIEW);
                    }
                } catch (Exception e) {
                    Toast.makeText(this, "error", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permission, @NonNull int[] grantResults) {
        if (permission == null || grantResults == null || grantResults.length < 2 || grantResults[0] != PackageManager.PERMISSION_GRANTED || grantResults[1] != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        if (requestCode == DEFINED_VIEW) {
            ScanUtil.startScan(this, REQUEST_CODE_SCAN, new HmsScanAnalyzerOptions.Creator().setHmsScanTypes(HmsScan.ALL_SCAN_TYPE).create());

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //Receive result
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK || data == null) {
            return;
        }

        if (requestCode == REQUEST_CODE_SCAN) {
            Object obj = data.getParcelableExtra(ScanUtil.RESULT);
            if (obj instanceof HmsScan) {
                if (!TextUtils.isEmpty(((HmsScan) obj).getOriginalValue())) {
                    try {
                        Intent intent_StartActivity = new Intent();
                        intent_StartActivity.setAction(Intent.ACTION_VIEW);
                        intent_StartActivity.setData(Uri.parse(((HmsScan) obj).getOriginalValue()));
                        startActivity(intent_StartActivity);
                        mEdittext.setText("");
                    } catch (Exception e) {
                        Toast.makeText(this, "链接错误，请输入有效链接", Toast.LENGTH_SHORT).show();
                    }
                }
                return;
            }
        }
    }

}