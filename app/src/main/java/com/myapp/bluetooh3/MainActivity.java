package com.myapp.bluetooh3;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraControl;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.extensions.HdrImageCaptureExtender;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothA2dp;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.common.util.concurrent.ListenableFuture;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }

    ListView listView;

    private static final int MY_PERMISSION_REQUEST_CONSTANT = 1;

    private Button button1;
    private Button top;
    private Button bottom;
    private Button left;
    private Button right;
    private Button stop;
    private LinearLayout linearLayout;

    private BluetoothAdapter mBluetoothAdapter;
    private BroadcastReceiver mBluetoothReceiver;//??????????????????????????????????????????????????????
    private String TAG = "MainActivity";
    private BroadcastReceiver mBLuetoothStateReceiver;

    private List<DeviceInformation> mDatas = new ArrayList<>();

    private FruitAdapter adapter;

    private BluetoothSocket mBluetoothSocket;

    private SeekBar seekBar;
    private int progess=100;

    private Mypreview mPreviewView;
    private TextView textView;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        openBluetooth();
        initReceiver();

        initCamera();

        //??????6.0??????????????????????????????
        if (Build.VERSION.SDK_INT >= 6.0) {
            ActivityCompat.requestPermissions(this, new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSION_REQUEST_CONSTANT);
        }


        linearLayout=findViewById(R.id.linear);
        button1=findViewById(R.id.btn1);

        top=findViewById(R.id.top);
        bottom=findViewById(R.id.bottom);
        left=findViewById(R.id.left);
        right=findViewById(R.id.right);
        seekBar=findViewById(R.id.seekBar);
        stop=findViewById(R.id.stop);

        mPreviewView=findViewById(R.id.mypreview);
        textView=findViewById(R.id.textView);




        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                linearLayout.setVisibility(View.VISIBLE);
                discoverBluetooth();
            }
        });


        top.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isconnect){
                    Toast.makeText(MainActivity.this,"??????????????????",Toast.LENGTH_SHORT).show();
                    return;
                }

                sendMessage("t");
            }
        });
        bottom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isconnect){
                    Toast.makeText(MainActivity.this,"??????????????????",Toast.LENGTH_SHORT).show();
                    return;
                }
                sendMessage("b");
            }
        });
        left.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()){
                    case KeyEvent.ACTION_DOWN:
                    {
                        if (!isconnect){
                            Toast.makeText(MainActivity.this,"??????????????????",Toast.LENGTH_SHORT).show();
                            break;
                        }
                        sendMessage("l");

                        break;
                    }
                    case KeyEvent.ACTION_UP:
                    {
                        if (!isconnect){
                            break;
                        }

                        sendMessage("s");

                        break;
                    }

                }


                return true;
            }
        });
        right.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()){
                    case KeyEvent.ACTION_DOWN:
                    {
                        if (!isconnect){
                            Toast.makeText(MainActivity.this,"??????????????????",Toast.LENGTH_SHORT).show();
                            break;
                        }
                        sendMessage("r");

                        break;
                    }
                    case KeyEvent.ACTION_UP:
                    {
                        if (!isconnect){
                            break;
                        }

                        sendMessage("s");

                        break;
                    }

                }


                return true;
            }
        });
        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isconnect){
                    Toast.makeText(MainActivity.this,"??????????????????",Toast.LENGTH_SHORT).show();
                    return;
                }

                sendMessage("s");
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                progess=progress;
                Log.i("aa","??????????????????"+progess);

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (!isconnect){
                    Toast.makeText(MainActivity.this,"??????????????????",Toast.LENGTH_SHORT).show();
                    return;
                }
                //????????????
                sendMessage(""+progess);

            }
        });





        listView=findViewById(R.id.list_view);
//        List<DeviceInformation> bluelist = new ArrayList<>();

        //????????????????????????????????????????????????
        //??????????????????????????? ?????? -??????-??????
        adapter=new FruitAdapter(MainActivity.this,R.layout.fruit_item,mDatas);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                if (mBluetoothAdapter.isDiscovering()) {
                    //??????????????????
                    mBluetoothAdapter.cancelDiscovery();
                }
                //???????????????item???????????????
                DeviceInformation blue= mDatas.get(position) ;
                Toast.makeText(MainActivity.this,"?????????????????????"+blue.getDeviceName(),Toast.LENGTH_SHORT).show();
                mAddress=blue.getDeviceAddress()+"";
                connectDevice();
            }
        });


    }

    private void discoverBluetooth(){
        if (mBluetoothAdapter.isDiscovering()) {
            mBluetoothAdapter.cancelDiscovery();
        }
        //????????????
        mBluetoothAdapter.startDiscovery();
        Toast.makeText(MainActivity.this,"??????????????????",Toast.LENGTH_LONG).show();
    }
    private void openBluetooth() {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();


        Log.i("aa",""+mBluetoothAdapter);
        if (mBluetoothAdapter != null) {
            //?????????????????????????????????
            if (!mBluetoothAdapter.isEnabled()) {
                //?????????????????????
                Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(intent,1);
            }
        }else{
            Toast.makeText(MainActivity.this,"???????????????????????????",Toast.LENGTH_LONG).show();
        }
    }

    private void initReceiver() {
        //????????????????????????????????????????????????????????????
        mBLuetoothStateReceiver = new BroadcastReceiver(){
            @Override
            public void onReceive(Context context, Intent intent) {
                int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1);
                switch (state){
                    case BluetoothAdapter.STATE_ON:
                        Toast.makeText(MainActivity.this,"???????????????",Toast.LENGTH_SHORT).show();
                        break;
                    case BluetoothAdapter.STATE_OFF:
                        Toast.makeText(MainActivity.this,"???????????????",Toast.LENGTH_SHORT).show();
                        break;
                    case BluetoothAdapter.STATE_TURNING_ON:
                        Toast.makeText(MainActivity.this,"??????????????????",Toast.LENGTH_SHORT).show();
                        break;
                    case BluetoothAdapter.STATE_TURNING_OFF:
                        Toast.makeText(MainActivity.this,"??????????????????",Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        };
        //?????????????????????????????????
        mBluetoothReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d(TAG,"onReceive");

                Log.i("aa","????????????");

                String action = intent.getAction();
                if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                    boolean isAdded = false;//?????????????????????????????????????????????????????????

                    //????????????????????????
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    Log.i("aa",""+device.getName());

                    //????????????
//                    if (device.getName()!=null && device.getName().equals("ESP32test")){
//                        Log.i("aa","????????????");
//                        device.createBond();
//                    }


                    //?????????????????????
                    DeviceInformation deviceInformation = new DeviceInformation(device.getName(),device.getAddress());
                    for (DeviceInformation data : mDatas) {
                        //???????????????????????????????????????????????????
                        if (data.getDeviceAddress().equals(deviceInformation.getDeviceAddress())) {
                            isAdded = true;
                            break;
                        }
                    }
                    if (!isAdded) {
                        //??????UI??????
                        mDatas.add(deviceInformation);
                        adapter.notifyDataSetChanged();
                    }
                }
            }
        };
        //?????????????????????
        IntentFilter filter1 = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        IntentFilter filter2 = new IntentFilter(BluetoothDevice.ACTION_FOUND);

        registerReceiver(mBLuetoothStateReceiver,filter1);
        registerReceiver(mBluetoothReceiver,filter2);
    }

    private BluetoothDevice mDevice;
    private String mAddress;
    private final UUID mUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");//?????????????????????UUID
    private boolean isconnect=false;
    private void connectDevice() {

        //?????????????????????????????????device
        mDevice = mBluetoothAdapter.getRemoteDevice(mAddress);

        try {
            //??????socket??????
            mBluetoothSocket = mDevice.createRfcommSocketToServiceRecord(mUUID);
            mBluetoothSocket.connect();
            if (mBluetoothSocket.isConnected()) {

                Toast.makeText(MainActivity.this,"????????????",Toast.LENGTH_SHORT).show();
                isconnect=true;
                linearLayout.setVisibility(View.GONE);

                //???????????????????????????
//                ReceiveDataThread thread = new ReceiveDataThread();
//                thread.start();
            }else{

                Toast.makeText(MainActivity.this,"????????????",Toast.LENGTH_SHORT).show();
                isconnect=false;
            }
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(MainActivity.this,"????????????",Toast.LENGTH_SHORT).show();
            isconnect=false;
            finish();
            try {
                mBluetoothSocket.close();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();

        unregisterReceiver(mBLuetoothStateReceiver);
        unregisterReceiver(mBluetoothReceiver);
        try {
            if (mBluetoothSocket.isConnected()) {
                //??????socket
                mBluetoothSocket.close();
                mBluetoothAdapter = null;

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * ?????????????????????
     * @param contentStr
     */
    private static OutputStream mOS;
    private void sendMessage(String contentStr) {
        if (mBluetoothSocket.isConnected()) {
            try {
                //???????????????
                mOS = mBluetoothSocket.getOutputStream();
                if (mOS != null) {
                    //?????????????????????byte?????????
                    mOS.write(contentStr.getBytes("GBK"));

//                    mSendContent.append(contentStr);

                    //Toast.makeText(MainActivity.this,"????????????",Toast.LENGTH_SHORT).show();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else{

            Toast.makeText(MainActivity.this,"?????????????????????",Toast.LENGTH_SHORT).show();
        }
    }



    private int REQUEST_CODE_PERMISSIONS = 1001;
    private void initCamera(){
        //????????????
        if(allPermissionsGranted()){
            startCamera(); //start camera if permission has been granted by user
        } else{
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS);
        }
    }
    private final String[] REQUIRED_PERMISSIONS = new String[]{"android.permission.CAMERA", "android.permission.WRITE_EXTERNAL_STORAGE"};
    //??????????????????
    private boolean allPermissionsGranted(){
        for(String permission : REQUIRED_PERMISSIONS){
            if(ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED){
                return false;
            }
        }
        return true;
    }

    private ProcessCameraProvider cameraProvider;
    private void startCamera() {

        final ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(this);

        cameraProviderFuture.addListener(new Runnable() {
            @Override
            public void run() {
                try {
                    cameraProvider = cameraProviderFuture.get();
                    bindPreview(cameraProvider);


                } catch (ExecutionException | InterruptedException e) {
                    // No errors need to be handled for this Future.
                    // This should never be reached.
                }
            }
        }, ContextCompat.getMainExecutor(this));
    }

    long t1=0;
    long t2=0;
    long t3=0;
    private int camea_id=1;
    private Bitmap bmp;
    private CameraControl cameraControl;

    private Configuration mConfiguration;

    float now_h=0.f;
    float now_x=0.f;

    private Executor executor = Executors.newSingleThreadExecutor();
    void bindPreview(@NonNull ProcessCameraProvider cameraProvider) {

        Preview preview = new Preview.Builder()
                .build();

        @SuppressLint("WrongConstant") CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(camea_id)
                .build();

        ImageAnalysis imageAnalysis = new ImageAnalysis.Builder()
                .build();
        //
        //imageAnalysis.setAnalyzer(cameraExecutor, new MyAnalyzer());
        imageAnalysis.setAnalyzer(executor, new ImageAnalysis.Analyzer() {
            @Override
            public void analyze(@NonNull ImageProxy image) {

                runOnUiThread(() ->{

                    t1=t2;
                    t2= System.currentTimeMillis();
                    long fps=1000/(t2-t1);

                    Message message=new Message();
                    message.what=1;
                    message.obj="FPS= "+fps;
                    handler.sendMessage(message);



//                    if (!start){
//                        image.close();
//                        return;
//                    }

                    //yuv???????????????bitmap
                    ImageProxy.PlaneProxy[] planes = image.getPlanes();

                    //cameraX ??????yuv
                    ByteBuffer yBuffer = planes[0].getBuffer();
                    ByteBuffer uBuffer = planes[1].getBuffer();
                    ByteBuffer vBuffer = planes[2].getBuffer();

                    int ySize = yBuffer.remaining();
                    int uSize = uBuffer.remaining();
                    int vSize = vBuffer.remaining();

                    byte[] nv21 = new byte[ySize + uSize + vSize];

                    yBuffer.get(nv21, 0, ySize);
                    vBuffer.get(nv21, ySize, vSize);
                    uBuffer.get(nv21, ySize + vSize, uSize);
                    //??????yuvImage
                    YuvImage yuvImage = new YuvImage(nv21, ImageFormat.NV21, image.getWidth(), image.getHeight(), null);
                    //?????????
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    //????????????out
                    yuvImage.compressToJpeg(new Rect(0, 0, yuvImage.getWidth(), yuvImage.getHeight()), 50, out);
                    //?????????
                    byte[] imageBytes = out.toByteArray();
                    //??????bitmap
                    Bitmap bmp = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);

                    //??????bitmap
                    Bitmap rotateBitmap=null;
//                    if (camea_id==1 && su){
//                        rotateBitmap = rotateBitmap(bmp, 90);
//                    }else if(camea_id==0 && su){
//                        rotateBitmap = rotateBitmap(bmp, 270);
//                    }else if(camea_id==1 && heng){
//                        rotateBitmap=bmp;
//                    }else {
//                        rotateBitmap=rotateBitmap(bmp, 0);
//                    }
                    rotateBitmap=rotateBitmap(bmp, 0);


                    Bitmap bmp2=rotateBitmap.copy(Bitmap.Config.ARGB_8888, true);

                    //SSd.Obj[] outcome=sSdnet.Detect(bmp2,us_gpu,conf_thred);


                    //Log.i("aa","bitmap_w="+bmp2.getWidth()+"  bitmap_h="+bmp2.getHeight());
//                    imageView.setImageBitmap(bmp2);

                    float aa=0;
                    float bb=0;

                    //SSd.Obj[] pick_outcom=new SSd.Obj[1];
//                    float maxconf=0;
//                    int index=0;
//                    if(outcome.length>0){
//
//                        for (int i=0;i<outcome.length;i++){
//                            if (i==0){
//                                maxconf=outcome[i].prob;
//                                index=i;
//                            }else {
//                                if(outcome[i].prob>maxconf){
//                                    maxconf=outcome[i].prob;
//                                    index=i;
//                                }
//                            }
//                        }
//
//                        pick_outcom[0]=outcome[index];
//
//                    }



//                    if (outcome.length>0){
//                        aa=Math.abs(pick_outcom[0].h*bmp2.getHeight()/now_h-1.f);
//                        if (now_x==0){
//                            bb=0;
//                        }else {
//                            bb=Math.abs(pick_outcom[0].x*bmp2.getHeight()/now_x-1.f);
//                        }
//                        music= aa > music_thred || bb > music_thred;
//
//
//                        if (music && !mediaPlayer.isPlaying()){
//
//                            mediaPlayer.start();
//
//                        }
//                        Log.i("aa","isplaying="+isPlaying);
//
//
////                        Log.i("aa"," aa="+aa+"   bb="+bb);
//
//                        if (t2-t3>3000){
//                            t3=t2;
//                            now_h=pick_outcom[0].h*bmp2.getHeight();
//                            now_x=pick_outcom[0].x*bmp2.getHeight();
//                        }
//
//                    }

//                    new Thread(new Runnable() { // ????????????Runnable??????
//                        @Override
//                        public void run() {
//                            myview.draws(outcome,bmp2.getWidth(),bmp2.getHeight(),heng,mediaPlayer.isPlaying());
//                        }
//                    }).start();


                    //??????
                    image.close();

                });

            }
        });

        ImageCapture.Builder builder = new ImageCapture.Builder();

        //Vendor-Extensions (The CameraX extensions dependency in build.gradle)
        HdrImageCaptureExtender hdrImageCaptureExtender = HdrImageCaptureExtender.create(builder);

        // Query if extension is available (optional).
        if (hdrImageCaptureExtender.isExtensionAvailable(cameraSelector)) {
            // Enable the extension if available.
            hdrImageCaptureExtender.enableExtension(cameraSelector);
        }

        final ImageCapture imageCapture = builder
                .setTargetRotation(this.getWindowManager().getDefaultDisplay().getRotation())
                .build();

        preview.setSurfaceProvider(mPreviewView.createSurfaceProvider());

        try {
            cameraProvider.unbindAll();
            Camera camera = cameraProvider.bindToLifecycle((LifecycleOwner)this, cameraSelector, preview, imageAnalysis, imageCapture);
            cameraControl=camera.getCameraControl();
            mPreviewView.cameraControl=cameraControl;
//            cameraControl.setLinearZoom(1f);


        } catch (Exception e) {
            e.printStackTrace();
        }


    }
    private Bitmap rotateBitmap(Bitmap origin, float alpha) {
        if (origin == null) {
            return null;
        }
        int width = origin.getWidth();
        int height = origin.getHeight();
        Matrix matrix = new Matrix();
        matrix.setRotate(alpha);
        if (camea_id==0){
            matrix.postScale(-1,1);
        }
        // ????????????????????????
        Bitmap newBM = Bitmap.createBitmap(origin, 0, 0, width, height, matrix, false);
        if (newBM.equals(origin)) {
            return newBM;
        }
        origin.recycle();
        return newBM;
    }

    private Handler handler=new Handler(Looper.myLooper()){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (msg.what==1){
                textView.setText((String)msg.obj);

            }
        }
    };






}