package com.example.myapplication;

import android.animation.Animator;
import android.annotation.SuppressLint;
//import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import org.tensorflow.lite.Interpreter;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.view.GravityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.base.OnItemClickListener;
import com.example.myapplication.databinding.ActivityMainBinding;
import com.skydoves.elasticviews.ElasticAnimation;
import com.skydoves.elasticviews.ElasticFinishListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.function.Predicate;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

//////////////////////
///////테스트 앱/////////
//////////////////////

public class MainActivity extends AppCompatActivity {

    ///////////////가상 전달 변수/////////////////////
    public String u;
    public int sender_airVolume = 0; //풍량 -> a:
    public int sender_timer = 0; //타이머 시간 -> t:
    public boolean sender_rotate = false; //회전
    public boolean sender_auto = false; //-> autoMode
    public String outerTP;
    public String inTP;
    public String outerWT;
    public String inWT;
    public int count = 0;
    //public int fanFirst = 0;
    public int setter_av = 0;
    public int sender_heatLv;
    public String sender_user;
    public int sender_firstCheck;
    public String sender_input;
    public String[] sender_array ={"00.0","00.0"};
    //////////////////////////////////////////////

    ////////////블루투스 ///////////////////////////
    private final int REQUEST_BLUETOOTH_ENABLE = 100;
    private ActivityResultLauncher<Intent> resultLauncher;
    public String fanName;
    public int fanFirst=0;
    public int autoCheck = 0;
    public int powerCheck = 0;
    private TextView mConnectionStatus;
    private EditText mInputEditText;

    ConnectedTask mConnectedTask = null;
    static BluetoothAdapter mBluetoothAdapter;
    private String mConnectedDeviceName = null;
    static boolean isConnectionError = false;
    private static final String TAG = "BluetoothClient";
    //////////////////////////////////////////////

    private DrawerLayout drawerLayout;
    private View drawerView;
    public int airVolume;
    public int headDegree = 0;

    public int selectedHeatLvel = 0;
    private boolean sendUser;


    private Dialog fanDialog;
    private Dialog addFanDialog;
    private Dialog updateFanDialog;
    TextView selectedSeason;
    RadioGroup radioGroup;

    private ActivityMainBinding binding;
    private boolean isCotentToggle = false;

    public int sender_dftAirVolume = 0; //디폴트 풍량
    public int saveAV;

    private FanAdapter adapter = null;
    private FanUpdateAdapter fanUpdateAdapter = null;
    private List<FanItem> fanList = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        LinearLayout first = (LinearLayout) findViewById(R.id.first);
        LinearLayout second = (LinearLayout) findViewById(R.id.second);

        /*setContentView(R.layout.activity_main);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide(); */

        //블루투스 시작
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            showErrorDialog("This device is not implement Bluetooth.");
            return;
        }


        //블루투스 연결되면 블루투스 리스트 보여줌
        if(autoCheck == 0) {
            if (!mBluetoothAdapter.isEnabled()) {
                Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                intent.putExtra("Call", REQUEST_BLUETOOTH_ENABLE);
                resultLauncher.launch(intent);
            } else {
                Log.d(TAG, "Initialisation successful.");

                showPairedDevicesListDialog();

            }
        }
        resultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if(result.getResultCode() == RESULT_OK){ //블루투스가 활성화되었음
                            Intent intent = result.getData();
                            int CallType = intent.getIntExtra("CallType", 0);
                            if(CallType == 0){
                                //실행될 코드
                            }else if(CallType == 1){
                                //실행될 코드
                            }else if(CallType == 2){
                                //실행될 코드
                            }
                        }
                    }
                });

        /////블루투스 끝

        /* 저장된 기본 풍속  불러옴 */
        sender_dftAirVolume = PreferenceManager.getInt(this, "dftAV_key");

        adapter = new FanAdapter(MainActivity.this);
        fanUpdateAdapter = new FanUpdateAdapter(MainActivity.this);

        fanList = PreferenceHelper.getFanList(getApplicationContext());
        if (fanList == null) {
            fanList = new ArrayList<>();
            fanList.add(new FanItem("My Fan", 1, 1));
            PreferenceHelper.setFanList(getApplicationContext(), fanList);
        }
        else {
            sender_heatLv = fanList.get(fanList.size() - 1).getFanHeat();
            sender_user= fanList.get(fanList.size() - 1).getFanName();
            sender_firstCheck= fanList.get(fanList.size() - 1).getFanFirst();
        }

        if (fanList != null && fanList.size() > 0) {
            binding.layoutNav.tvFanName.setText(fanList.get(fanList.size() - 1).getFanName());
            u = fanList.get(fanList.size() - 1).getFanName();
        }

        /* 사이드바 열기 */
        binding.btnMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        binding.btnAuto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //autocheck가 1이면 auto가 켜
                isCotentToggle = !isCotentToggle;
                if(powerCheck == 1 && autoCheck == 0) isCotentToggle = true;


                //auto모드를 켰을 때
                if (isCotentToggle) {
                    Log.d(TAG, "auto Toggle On");

                    autoCheck = 1;
                    //binding.layoutContent.setVisibility(View.INVISIBLE);

                    //0맞
                    binding.layoutContent.animate().scaleX(0).scaleY(0).setDuration(300).start();

                    binding.btnPower.setVisibility(Button.INVISIBLE);
                    binding.layoutBtnPower.setVisibility(CardView.INVISIBLE);

                    binding.autoMode.setText("AUTO MODE");
                    binding.result.setVisibility(TextView.VISIBLE);
                    binding.result.setText(String.valueOf(setter_av));
                    //binding.result.setText("풍속");

                    //new FanItem(fanName, selectedHeatLvel,);
                    //startActivity(new Intent(MainActivity.this, AutoActivity.class));
                    if(sender_firstCheck==1) {
                        sendMessage("powerOon,autoOon," + String.valueOf(sender_firstCheck) +","+ String.valueOf(sender_heatLv) +","+sender_array[0]+","+sender_array[1]+","+ sender_user);
                        fanList.get(fanList.size() - 1).setFanFirst(0);
                        sender_firstCheck=0;
                    }
                    else if(sender_firstCheck==0) {
                        sendMessage("powerOon,autoOon," + String.valueOf(sender_firstCheck) +","+sender_array[0]+","+sender_array[1]+","+ sender_user);
                    }
                    Log.d(TAG, "autoCheck :  "+String.valueOf(autoCheck));
                }
                //auto모드를 껐을 때
                else {

                    Log.d(TAG, "auto Toggle Off");
                    autoCheck = 0;
                    binding.autoMode.setText(" ");
                    binding.result.setText(" ");
                    binding.btnPower.setVisibility(Button.VISIBLE);
                    binding.layoutBtnPower.setVisibility(CardView.VISIBLE);

                    binding.layoutContent.animate().scaleX(0).scaleY(0).setDuration(400).start();
                    binding.tvFan.setText("현재 풍량은 " + setter_av + "입니다.");
                    binding.sbFan.setProgress(setter_av);

                    if(sender_airVolume<10){
                        sendMessage("powerOon,autoOff,0" +String.valueOf(sender_airVolume) +","+sender_array[0]+","+sender_array[1]+","+ sender_user);
                        fanList.get(fanList.size() - 1).setFanFirst(0);
                        sender_firstCheck=0;
                    }
                    else {
                        sendMessage("powerOon,autoOff," +String.valueOf(sender_airVolume) +","+sender_array[0]+","+sender_array[1]+","+ sender_user);
                        fanList.get(fanList.size() - 1).setFanFirst(0);
                        sender_firstCheck=0;
                    }

                    //TODO
                   /* if(sender_firstCheck==1) {
                        sendMessage("powerOon,autoOon," + String.valueOf(sender_firstCheck) +","+ String.valueOf(sender_heatLv) +","+ sender_user);
                        fanList.get(fanList.size() - 1).setFanFirst(0);
                        sender_firstCheck=0;
                    }
                    else if(sender_firstCheck==0) {
                        sendMessage("powerOon,autoOon," + String.valueOf(sender_firstCheck) +","+ sender_user);
                    }*/
                    Log.d(TAG, "autoCheck :  "+String.valueOf(autoCheck));
                }




            }
        });

        binding.btnPower.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "autoCheck :  "+String.valueOf(autoCheck));
                //가운데 전원 버튼눌렀을시...
                //토글형태로 만들어주기위해 변수를 변환 시켜준다.
                isCotentToggle = !isCotentToggle;

                if (isCotentToggle) {
                    //전원 버튼 활성화시
                    powerCheck = 1;
                    Log.d(TAG, "Power Toggle On");
                     if(sender_airVolume<10){
                         sendMessage("powerOon,autoOff,0" +String.valueOf(sender_airVolume) +","+sender_array[0]+","+sender_array[1]+","+ sender_user);
                     }
                     else {
                         sendMessage("powerOon,autoOff," +String.valueOf(sender_airVolume) +","+sender_array[0]+","+sender_array[1]+","+ sender_user);
                     }
                    //binding.btnPower.setVisibility(Button.INVISIBLE);
                    binding.result.setVisibility(TextView.INVISIBLE);
                    binding.layoutContent.animate().scaleX(0.9f).scaleY(0.9f).setDuration(400).start();

                    /*if(binding.btnAuto.isPressed()){
                        binding.layoutContent.animate().scaleX(0).scaleY(0).setDuration(400).start();
                        binding.autoMode.setText("erwoeir");
                        binding.result.setText(String.valueOf(setter_av));
                        autoCheck=0;
                    }
                    else {
                        binding.layoutContent.animate().scaleX(0.9f).scaleY(0.9f).setDuration(400).start();
                    } */
                } else {
                    powerCheck = 0;
                    Log.d(TAG, "Power Toggle Off");
                    //전원 버튼 토글시. 화면이 이미 보여지고있기에 다시 숨길때사용
                    binding.layoutContent.animate().scaleX(0).scaleY(0).setDuration(300).start();
                    /*if(autoCheck==1){
                        binding.autoMode.setText(" ");
                        binding.result.setText(" ");
                        autoCheck=0;
                    } */
                    if(sender_airVolume<10){
                        sendMessage("powerOff,autoOff,0" +String.valueOf(sender_airVolume) +","+sender_array[0]+","+sender_array[1]+","+ sender_user);
                    }
                    else sendMessage("powerOff,autoOff," +String.valueOf(sender_airVolume) +","+sender_array[0]+","+sender_array[1]+","+ sender_user);
                }

            }
        });

        //타이머
        binding.btnTimer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder ad = new AlertDialog.Builder(MainActivity.this);
                ad.setIcon(R.drawable.ic_baseline_timer_24);
                //ad.setTitle("timer");
                ad.setMessage("타이머 입력");

                final EditText et = new EditText(MainActivity.this);
                ad.setView(et);

                ad.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String result = et.getText().toString();
                        //sender_timer = Integer.parseInt(result);
                        dialog.dismiss();
                    }
                });
                ad.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                ad.show();

            }
        });

        binding.btnRotate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //360도로 계속 회전한다고 한다면
                while (true) {
                    if (headDegree == 180) { //오른쪽으로 180이되면
                        headDegree += -15;
                    } //왼쪽으로 이동
                    else if (headDegree == -180) {
                        headDegree += +15;
                    }
                    break;
                }
            }
        });

        //TODO
        binding.btnLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.btnLeft.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v){
                        headDegree += -15; //
                        //한번 누를 때마다 왼쪽으로 15도씩 이동, 각도 변경
                    }
                });
            }
        });

        //TODO
        binding.btnRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.btnRight.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        headDegree += 15;
                        //한번 누를 때마다 오른쪽으로 15도씩 이동, 각도 변경
                    }
                });
            }
        });

        /* 풍량 조절 */
        binding.sbFan.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                binding.tvFan.setText("현재 풍량은 " + progress + "입니다.");

                airVolume = progress;

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            /* 드래드가 멈추면 기계쪽으로 숫자가 넘어가도록 구현했음 */
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                sender_airVolume = airVolume;

                if(sender_airVolume<10){
                    sendMessage("powerOon,autoOff,0" +String.valueOf(sender_airVolume) +","+sender_array[0]+","+sender_array[1]+","+ sender_user);
                }
                else sendMessage("powerOon,autoOff," +String.valueOf(sender_airVolume) +","+sender_array[0]+","+sender_array[1]+","+ sender_user);
            }
        });

        /////////////////////////////
        //////여기부터 사이드바///////////
        /////////////////////////////

        /* 사이드바 닫힘버튼 */
        binding.layoutNav.btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeDrawer();
            }
        });

        //사용자 바꾸기.

        binding.layoutNav.btnOuterFan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
                View view = inflater.inflate(R.layout.dialog_select_fan, null);
                fanDialog = new Dialog(MainActivity.this, R.style.dialog_type_theme);
                Button addFan = (Button) view.findViewById(R.id.add_fan);
                Button removeFan = (Button) view.findViewById(R.id.remove_fan);
                RecyclerView rvFan = (RecyclerView) view.findViewById(R.id.rv_fan);

                rvFan.setAdapter(adapter);

                adapter.updateItems(fanList);

                adapter.setOnItemClickListener(new OnItemClickListener() {
                    @Override
                    public void onItemClick(int position) {
                        binding.layoutNav.tvFanName.setText(adapter.getItem(position).getFanName());
                        sender_user = adapter.getItem(position).getFanName();
                        sender_heatLv = fanList.get(fanList.size() - 1).getFanHeat();
                        sender_firstCheck= fanList.get(fanList.size() - 1).getFanFirst();
                        fanDialog.dismiss();
                    }
                });

                addFan.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showAddFanDialog();
                    }
                });

                removeFan.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showRemoveDialog();
                    }
                });
                fanDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                fanDialog.setContentView(view);
                fanDialog.show();


            }
        });

//        //사용자 정보 설정
//        final RadioGroup rg = (RadioGroup) findViewById(R.id.radio_group);
//        Button b = (Button) findViewById(R.id.button1);
//        final TextView tv = (TextView) findViewById(R.id.textView2);
//
        /*binding.layoutNav.btnHeatAmount.setOnClickListener(new View.OnClickListener() {//          @Override
            public void onClick(View v) {
                //사용자 정보 설정
                LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
                View view = inflater.inflate(R.layout.dialog_update_fan, null);
                updateFanDialog = new Dialog(MainActivity.this, R.style.dialog_type_theme);
                Button updateFan = (Button) view.findViewById(R.id.update_fan);
                RecyclerView rvFan = (RecyclerView) view.findViewById(R.id.rv_fan);

                ArrayList<FanItem> list = new ArrayList<>();
                for (int i = 0; i < fanList.size(); i++) {
                    if (fanList.get(i).getFanName().contentEquals(binding.layoutNav.tvFanName.getText())) {
                        list.add(fanList.get(i));
                        break;
                    }
                }

                rvFan.setAdapter(fanUpdateAdapter);

                fanUpdateAdapter.updateItems(list);


                updateFan.setOnClickListener(v1 -> {
                    if (list.size() > 0) {
                        showHotLevelUpdateDialog(list.get(0));
                    }

                });

                updateFanDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                updateFanDialog.setContentView(view);
                updateFanDialog.show();
            }
        }); */

        //기본풍속설정
        binding.layoutNav.btnDefaultAV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dftAV_ab = new AlertDialog.Builder(MainActivity.this);
                dftAV_ab.setTitle("형식: 온도,습도(콤마필수!)");
                dftAV_ab.setMessage("입력 : ");

                final EditText dftAV_et = new EditText(MainActivity.this);
                dftAV_ab.setView(dftAV_et);

                dftAV_ab.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String dftAV_result = dftAV_et.getText().toString();
                        sender_input = dftAV_result;
                        //saveAV = Integer.parseInt(dftAV_result);
                        //PreferenceManager.setInt(MainActivity.this, "dftAV_key", saveAV);
                        dialog.dismiss();

                        new AlertDialog.Builder(MainActivity.this) //저장 확인 메세지
                                .setMessage("온도,습도 : " + sender_input)
                                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        //TODO
                                        //임의 온습도
                                        sender_array = sender_input.split(",");
                                        binding.outerTemperature.setText(sender_array[0]+"C");
                                        binding.outerWater.setText(sender_array[1]+"%");
                                        Toast.makeText(getApplicationContext(),sender_input, Toast.LENGTH_SHORT).show(); // 실행할 코드
                                        if(autoCheck==1){
                                            if(sender_firstCheck==1) {
                                                sendMessage("powerOon,autoOon," + String.valueOf(sender_firstCheck) +","+ String.valueOf(sender_heatLv) +","+sender_array[0]+","+sender_array[1]+","+sender_user);
                                            }
                                            else if(sender_firstCheck==0) {
                                                sendMessage("powerOon,autoOon," + String.valueOf(sender_firstCheck) +","+sender_array[0]+","+sender_array[1]+","+sender_user);
                                            }
                                            binding.autoMode.setText("AUTO MODE");
                                            Log.d(TAG, "setResult :" + Integer.toString(setter_av));
                                            Log.d(TAG, "autoCheck :  "+String.valueOf(autoCheck));
                                            binding.result.setText(String.valueOf(setter_av));
                                        }
                                    }
                                })
                                .show();
                    }
                });
                dftAV_ab.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                dftAV_ab.show();
            }
        });

    }

    private void showRemoveDialog() {
        //삭제 다이얼로그생성...
        LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
        View view = inflater.inflate(R.layout.dialog_remove_fan, null);
        Dialog dialog = new Dialog(MainActivity.this, R.style.dialog_type_theme);
        RecyclerView rvFan = (RecyclerView) view.findViewById(R.id.rv_remove);

        rvFan.setAdapter(adapter);

        adapter.updateItems(fanList);
        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                L.i("::::삭제할 선풍기클릭 " + position);
                FanItem data = adapter.getItem(position);
                MessageUtils.showShortToastMsg(getApplicationContext(), data.getFanName() + "선풍기가 삭제되었습니다.");
                adapter.removeItem(position);
                fanList.remove(position);
                PreferenceHelper.setFanList(getApplicationContext(), adapter.getItemList());
                dialog.dismiss();
                fanDialog.dismiss();
            }
        });

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(view);
        dialog.show();


    }


    private void showAddFanDialog() {
        LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
        View view = inflater.inflate(R.layout.dialog_write_fan, null);
        addFanDialog = new Dialog(MainActivity.this, R.style.dialog_type_theme);
        EditText etName = (EditText) view.findViewById(R.id.et_name);
        Button apply = (Button) view.findViewById(R.id.btn_apply);


        apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(etName.getText())) {
                    return;
                }
                showHotLevelDialog(etName.getText().toString());
            }
        });
        addFanDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        addFanDialog.setContentView(view);
        addFanDialog.show();
    }


    private void showHotLevelDialog(String fanName) {


        //더위 선택 화면
        LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
        View view = inflater.inflate(R.layout.dialog_select_heat, null);
        Dialog dialog = new Dialog(MainActivity.this, R.style.dialog_type_theme);
        RadioGroup rgHeatLevel = (RadioGroup) view.findViewById(R.id.radio_group);
        Button apply = (Button) view.findViewById(R.id.btn_apply);

        selectedHeatLvel = 2;
        rgHeatLevel.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.radio1:
                        selectedHeatLvel = 2;
                        break;
                    case R.id.radio2:
                        selectedHeatLvel = 1;
                        break;
                    case R.id.radio3:
                        selectedHeatLvel = 0;
                        break;
                }
            }
        });


        apply.setOnClickListener(v -> {

            MessageUtils.showShortToastMsg(getApplicationContext(), fanName + "사용자가 등록되었습니다.");

            //사용자 추가
            while (fanList.add(new FanItem(fanName, selectedHeatLvel,1))){
                sender_heatLv = selectedHeatLvel;
                sender_user = fanName;
                //sender_firstCheck = 0;
                break;
            }
            sendUser = false;
            PreferenceHelper.setFanList(getApplicationContext(), fanList);
            binding.layoutNav.tvFanName.setText(fanName);
            //TODO

            //fanList.add(new FanItem(fanName, selectedHeatLvel,1));
            PreferenceHelper.setFanList(getApplicationContext(), fanList);
            binding.layoutNav.tvFanName.setText(fanName);

            fanDialog.dismiss();
            addFanDialog.dismiss();
            dialog.dismiss();
        });

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(view);
        dialog.show();
    }


    /*//TODO
    private void showHotLevelUpdateDialog(FanItem fanItem) {
        //더위 수정 화면
        LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
        View view = inflater.inflate(R.layout.dialog_select_heat, null);
        Dialog dialog = new Dialog(MainActivity.this, R.style.dialog_type_theme);
        RadioGroup rgHeatLevel = (RadioGroup) view.findViewById(R.id.radio_group);
        Button apply = (Button) view.findViewById(R.id.btn_apply);

        selectedHeatLvel = fanItem.getFanHeat();

        int checkId;

        if (fanItem.getFanHeat() == 0) {
            checkId = R.id.radio1;
        } else if (fanItem.getFanHeat() == 1) {
            checkId = R.id.radio2;
        } else {
            checkId = R.id.radio3;
        }

        rgHeatLevel.check(checkId);


        rgHeatLevel.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.radio1:
                        selectedHeatLvel = 0;
                        break;
                    case R.id.radio2:
                        selectedHeatLvel = 1;
                        break;
                    case R.id.radio3:
                        selectedHeatLvel = 2;
                        break;
                }
            }
        });


        apply.setOnClickListener(v -> {

            HeatLevel heatType;

            if (selectedHeatLvel == 0) {
                heatType = HeatLevel.LEVEL1;
            } else if (selectedHeatLvel == 1) {
                heatType = HeatLevel.LEVEL2;
            } else {
                heatType = HeatLevel.LEVEL3;
            }

            int index = fanList.indexOf(fanItem);

            fanItem.setFanHeat(selectedHeatLvel);
            fanList.remove(index);
            fanList.add(index,fanItem);

            L.i(":::::::::index " + index);

            MessageUtils.showShortToastMsg(getApplicationContext(), fanItem.getFanName() + "사용자는" + "\n" + heatType.content + "로 변경되었습니다.");

            PreferenceHelper.setFanList(getApplicationContext(), fanList);

            updateFanDialog.dismiss();
            dialog.dismiss();
        });
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(view);
        dialog.show();
    }*/

    @Override
    public void onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            powerCheck = 0;
            if(sender_airVolume<10){
                if(sender_airVolume<10){
                    sendMessage("powerOon,autoOff,0" +String.valueOf(sender_airVolume) +","+sender_array[0]+","+sender_array[1]+","+ sender_user);
                }
                else sendMessage("powerOon,autoOff," +String.valueOf(sender_airVolume) +","+sender_array[0]+","+sender_array[1]+","+sender_user);
            }

            closeDrawer();
            return;
        }
        super.onBackPressed();
    }

    private void closeDrawer() {
        binding.drawerLayout.closeDrawer(GravityCompat.START);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if ( mConnectedTask != null ) {
            powerCheck = 0;

                if(sender_airVolume<10){
                    sendMessage("powerOon,autoOff,0" +String.valueOf(sender_airVolume) +","+sender_array[0]+","+sender_array[1]+","+ sender_user);
                }
                else sendMessage("powerOon,autoOff," +String.valueOf(sender_airVolume) +","+sender_array[0]+","+sender_array[1]+","+sender_user);

            mConnectedTask.cancel(true);
        }
    }


    /*디바이스 연결*/
    private class ConnectTask extends AsyncTask<Void, Void, Boolean> {

        private BluetoothSocket mBluetoothSocket = null;
        private BluetoothDevice mBluetoothDevice = null;

        //시리얼 통신(SPP)을 하기위한 RFCOMM 블루투스 소켓 생성
        ConnectTask(BluetoothDevice bluetoothDevice) {
            mBluetoothDevice = bluetoothDevice;
            mConnectedDeviceName = bluetoothDevice.getName();

            //SPP
            UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");

            try {
                mBluetoothSocket = mBluetoothDevice.createRfcommSocketToServiceRecord(uuid);
                //Log.~~ 로그캣에 찍기위해 사용
                Log.d(TAG, "create socket for "+mConnectedDeviceName);

            } catch (IOException e) {
                Log.e( TAG, "socket create failed " + e.getMessage());
            }

            //TODO 오류부분
            //mConnectionStatus.setText("connecting...");
            Log.d(TAG, "요기 ");
        }


        @Override
        protected Boolean doInBackground(Void... params) {

            // Always cancel discovery because it will slow down a connection
            mBluetoothAdapter.cancelDiscovery(); //디바이스 검색 중지

            // Make a connection to the BluetoothSocket
            try {
                // This is a blocking call and will only return on a
                // successful connection or an exception
                mBluetoothSocket.connect();
            } catch (IOException e) {
                // Close the socket
                try {
                    mBluetoothSocket.close();
                } catch (IOException e2) {
                    Log.e(TAG, "unable to close() " +
                            " socket during connection failure", e2);
                }

                return false;
            }

            return true;
        }


        /*소켓 생성 성공시 connectedTask AsyncTask실해ㅎ*/
        @Override
        protected void onPostExecute(Boolean isSucess) {

            if ( isSucess ) {
                connected(mBluetoothSocket);
            }
            else{

                isConnectionError = true;
                Log.d( TAG,  "Unable to connect device");
                showErrorDialog("Unable to connect device");
            }
        }
    }


    //소켓 실행
    public void connected( BluetoothSocket socket ) {
        mConnectedTask = new ConnectedTask(socket);
        mConnectedTask.execute();
    }



    /*실제 데이터 주고 받기*/
    private class ConnectedTask extends AsyncTask<Void, String, Boolean> {

        private InputStream mInputStream = null;
        private OutputStream mOutputStream = null;
        private BluetoothSocket mBluetoothSocket = null;

        ConnectedTask(BluetoothSocket socket){

            mBluetoothSocket = socket;
            try {
                mInputStream = mBluetoothSocket.getInputStream();
                mOutputStream = mBluetoothSocket.getOutputStream();
            } catch (IOException e) {
                Log.e(TAG, "socket not created", e );
            }

            Log.d( TAG, "connected to "+mConnectedDeviceName);
           // mConnectionStatus.setText( "connected to "+mConnectedDeviceName);
        }


        //이 메소드에서 대기하며 수신되는 문자열이 있을 시 버퍼에 저장
        @Override
        protected Boolean doInBackground(Void... params) {

            byte [] readBuffer = new byte[1024];
            int readBufferPosition = 0;


            while (true) {

                if (isCancelled()) return false;

                try {

                    int bytesAvailable = mInputStream.available();

                    if (bytesAvailable > 0) {

                        byte[] packetBytes = new byte[bytesAvailable];

                        mInputStream.read(packetBytes);

                        for (int i = 0; i < bytesAvailable; i++) {

                            byte b = packetBytes[i];
                            if (b == ',') {
                                byte[] encodedBytes = new byte[readBufferPosition];
                                System.arraycopy(readBuffer, 0, encodedBytes, 0,
                                        encodedBytes.length);
                                String recvMessage = new String(encodedBytes, "UTF-8");

                                readBufferPosition = 0;

                                Log.d(TAG, "recv message: " + recvMessage);
                                publishProgress(recvMessage);
                            }else if(b=='\n'){
                                //count =0;
                            }
                            else {
                                readBuffer[readBufferPosition++] = b;
                            }
                        }
                    } //c +ctl
                } catch (IOException e) {

                    Log.e(TAG, "disconnected", e);
                    return false;
                }
            }
        }


        @Override //받은 메세지 업데이트
        // 아래 코드는 리스트에 추가해주는 코드
        public void onProgressUpdate(String... recvMessage) {
                Log.d(TAG, "recv = " + recvMessage[0]);
                Log.d(TAG, "count = " + count);
                //테스트용이라서 업데이트 바깥온습도 업데이트 안되도록 해두었음

                if(count == 0) { /*binding.outerTemperature.setText(recvMessage[0]); */count++;}
                else if(count == 1) {binding.tvTemperature.setText(recvMessage[0]);  count++;}
                else if(count == 2) {/*binding.outerWater.setText(recvMessage[0]); */ count++;}
                else if(count == 3) { binding.tvWater.setText(recvMessage[0]); count++;}
                else if(count == 4) {
                    setter_av = Integer.valueOf(recvMessage[0]);
                    if(setter_av<0) {setter_av = 0;}
                    if(autoCheck==1) {
                        Log.d(TAG, "setResult :" + Integer.toString(setter_av));
                        binding.result.setText(String.valueOf(setter_av));
                    }
                    count = 0;
                }
           // mConversationArrayAdapter.insert(mConnectedDeviceName + ": " + recvMessage[0], 0);
        }

        @Override
        protected void onPostExecute(Boolean isSucess) {
            super.onPostExecute(isSucess);

            if ( !isSucess ) {


                closeSocket();
                Log.d(TAG, "Device connection was lost");
                isConnectionError = true;
                showErrorDialog("Device connection was lost");
            }
        }

        @Override
        protected void onCancelled(Boolean aBoolean) {
            super.onCancelled(aBoolean);

            closeSocket();
        }

        void closeSocket(){

            try {

                mBluetoothSocket.close();
                Log.d(TAG, "close socket()");

            } catch (IOException e2) {

                Log.e(TAG, "unable to close() " +
                        " socket during connection failure", e2);
            }
        }

        //TODO
        //받은 메세지 출력
        void write(String msg){

            msg += "\n";

            try {
                mOutputStream.write(msg.getBytes());
                mOutputStream.flush();
            } catch (IOException e) {
                Log.e(TAG, "Exception during send", e );
            }

        }
    }


    /* 페어링 되어있는 블루투스 장치들의 목록확인.*/
    protected void showPairedDevicesListDialog()
    {
        if(autoCheck==1) return;
        Set<BluetoothDevice> devices = mBluetoothAdapter.getBondedDevices();
        final BluetoothDevice[] pairedDevices = devices.toArray(new BluetoothDevice[0]);

        if ( pairedDevices.length == 0 ){ //디바이스 연결 확인
            showQuitDialog( "No devices have been paired.\n"
                    +"You must pair it with another device.");
            return;
        }

        String[] items;
        items = new String[pairedDevices.length];
        for (int i=0;i<pairedDevices.length;i++) {
            items[i] = pairedDevices[i].getName();
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select device");
        builder.setCancelable(false);
        builder.setItems(items,new DialogInterface.OnClickListener() {
            //디바이스 선택하면 기기 선택 종료,
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

                //다비이스 연결
                ConnectTask task = new ConnectTask(pairedDevices[which]);
                task.execute();
            }
        });
        builder.create().show();
    }



    public void showErrorDialog(String message)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Quit");
        builder.setCancelable(false);
        builder.setMessage(message);
        builder.setPositiveButton("OK",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                if ( isConnectionError  ) {
                    isConnectionError = false;
                    finish();
                }
            }
        });
        builder.create().show();
    }


    public void showQuitDialog(String message)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Quit");
        builder.setCancelable(false);
        builder.setMessage(message);
        builder.setPositiveButton("OK",  new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                finish();
            }
        });
        builder.create().show();
    }

    //보내기
    void sendMessage(String msg){

        if ( mConnectedTask != null ) {
            mConnectedTask.write(msg);
            Log.d(TAG, "send message: " + msg);

            //리스트뷰에 추가
           // mConversationArrayAdapter.insert("Me:  " + msg, 0);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if(autoCheck == 0) {
            if (requestCode == REQUEST_BLUETOOTH_ENABLE) {
                if (resultCode == RESULT_OK) {
                    //BlueTooth is now Enabled
                    showPairedDevicesListDialog();
                }
                if (resultCode == RESULT_CANCELED) {
                    showQuitDialog("You need to enable bluetooth");
                }
            }
        }
    }
}