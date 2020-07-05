package com.example.doveletter.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewStub;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.request.RequestOptions;
import com.example.doveletter.R;
import com.example.doveletter.app.MyApplication;
import com.example.doveletter.bean.DraftInfo;
import com.example.doveletter.bean.SentInfo;
import com.example.doveletter.utils.BitmapUtils;
import com.example.doveletter.utils.FileUtil;
import com.example.doveletter.utils.ImageUtil;
import com.example.doveletter.utils.SendUtil;
import com.example.doveletter.utils.StatusBarUtil;
import com.lcw.library.imagepicker.ImagePicker;
import com.lcw.library.imagepicker.utils.ImageLoader;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.core.BasePopupView;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.engine.impl.GlideEngine;
import com.zhihu.matisse.internal.entity.CaptureStrategy;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UploadFileListener;


public class WriteActivity extends AppCompatActivity implements View.OnClickListener{

    private static final int REQUEST_CODE_CHOOSE = 7;
    private static final int REQUEST_CODE_CONTACT=11;
    //    标题栏
    private ImageView iv_home;
    private TextView tv_title;
    private ImageView iv_operation;

    //    信息
    private EditText et_receiver;
    private EditText et_sender;
    private  EditText et_subject;
    private EditText et_content;
    private ImageButton btn_choose_contact;


    private ViewStub vs_add_attachment;
    private ImageView iv_add_attachment;
//    private Uri attachmentUri;
    private String attachmentPath;

    private boolean isShow = false;
    private boolean isAttachmentInit = false;

    private boolean isSend = false;

    private BasePopupView sendPopup;

    private static final int SEND_SUCCESS=3;
    private static final int SEND_FAILED=4;


    private Handler handler=new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what){
                case SEND_SUCCESS:
                    sendPopup.dismiss();
                    Toast.makeText(WriteActivity.this, "发送成功", Toast.LENGTH_SHORT).show();
                    saveSent();
                    et_receiver.getText().clear();
                    et_subject.getText().clear();
                    et_content.getText().clear();
                    break;
                case SEND_FAILED:
                    sendPopup.dismiss();
                    Toast.makeText(WriteActivity.this, "发送失败", Toast.LENGTH_SHORT).show();
                    break;
            }
            super.handleMessage(msg);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write);
        StatusBarUtil.setStatusBarMode(this, true, R.color.colorMain);
        initView();
        initData();
    }

    private void initView() {
        //标题栏
        iv_home=findViewById(R.id.iv_home);
        iv_home.setOnClickListener(this);
        iv_home.setImageDrawable(getResources().getDrawable(R.drawable.ic_arrow_back_black_24dp));
        tv_title=findViewById(R.id.tv_title);
        tv_title.setText("发邮件");
        iv_operation=findViewById(R.id.iv_operation);
        iv_operation.setOnClickListener(this);
        iv_operation.setImageDrawable(getResources().getDrawable(R.drawable.ic_send_white_24dp));



        et_receiver=findViewById(R.id.et_receiver);
        et_sender=findViewById(R.id.et_sender);
        et_subject=findViewById(R.id.et_subject);
        et_content=findViewById(R.id.et_content);
        et_sender.setText(MyApplication.userInfo.getUseraddress());
        vs_add_attachment = findViewById(R.id.vs_add_attachment);
        iv_add_attachment=findViewById(R.id.iv_add_attachment);
        iv_add_attachment.setOnClickListener(this);

        btn_choose_contact=findViewById(R.id.btn_choose_contact);
        btn_choose_contact.setOnClickListener(this);


    }

    private void initData(){
        if(getIntent()!=null){
            int id=getIntent().getIntExtra("jumpfrom",-1);
            switch (id){
                case 1:
                    String receiver=getIntent().getStringExtra("receiver");
                    String subject=getIntent().getStringExtra("subject");
                    String content=getIntent().getStringExtra("content");
                    et_receiver.setText(receiver);
                    et_subject.setText(subject);
                    et_content.setText(content);
                    break;
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_home:
                showCancelPopup();
                break;
            case R.id.iv_operation:
                checkAndSend();
                break;
            case R.id.iv_add_attachment:
                if (!isAttachmentInit) {
                    //openCamera();
                    selectFromAlbum();
                } else {
                    showAttachment();    //判断是否显示添加附件界面
                }
                isShow = !isShow;
                break;

            case R.id.btn_choose_contact:
                chooseContact();
        }
    }

    private void chooseContact(){
        Intent intent=new Intent(WriteActivity.this,ContactActivity.class);
//        intent.putExtra("id",5);
        startActivityForResult(intent,REQUEST_CODE_CONTACT);
    }


    private void checkAndSend(){
        if (!isSend) {
            String address = et_receiver.getText().toString();
            String subject = et_subject.getText().toString();
            String content = et_content.getText().toString();
            if (!address.isEmpty() && !subject.isEmpty() && !content.isEmpty()) {
                sendEmail(address, subject, content);
            } else {
                Toast.makeText(this,
                        "请填写完整各处信息", Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void sendEmail(final String address, final String subject, final String content) {
        isSend = true;
        sendPopup = new XPopup.Builder(this)
                .asLoading("正在发送中...")
                .show();
        sendPopup.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });

        /**
         *发送邮件
         */
        new Thread(){
            @Override
            public void run() {
                SendUtil sendUtil=new SendUtil();
                Message msg=new Message();
                if(!TextUtils.isEmpty(attachmentPath)){
                    try{
                        sendUtil.SendEmail(address,subject,content,  attachmentPath);
                        msg.what=SEND_SUCCESS;
                    }catch(Exception e){
                        e.printStackTrace();
                        msg.what=SEND_FAILED;
                    }
                }else{
                    try {
                        sendUtil.SendEmail(address,subject,content,"");
                        msg.what=SEND_SUCCESS;
                    }catch (Exception e){
                        e.printStackTrace();
                        msg.what=SEND_FAILED;
                    }
                }
                handler.sendMessage(msg);
            }
        }.start();
    }


    //新的图片选择器
    private void selectFromAlbum()
    {
        ImagePicker.getInstance()
                .setTitle("选择图片")//设置标题
                .showCamera(true)//设置是否显示拍照按钮
                .showImage(true)//设置是否展示图片
                .showVideo(false)//设置是否展示视频
                .setSingleType(true)//设置图片视频不能同时选择
                .setMaxCount(1)//设置最大选择图片数目(默认为1，单选)
                .setImageLoader(new WriteActivity.GlideLoader())//设置自定义图片加载器
                .start(WriteActivity.this, REQUEST_CODE_CHOOSE);//REQEST_SELECT_IMAGES_CODE为Intent调用的requestCode
    }


    public class GlideLoader implements ImageLoader {

        private RequestOptions mOptions = new RequestOptions()
                .centerCrop()
                .format(DecodeFormat.PREFER_RGB_565)
                .placeholder(R.mipmap.icon_image_default)
                ;

        private RequestOptions mPreOptions = new RequestOptions()
                .skipMemoryCache(true)
                ;

        @Override
        public void loadImage(ImageView imageView, String imagePath) {
            //小图加载
            Glide.with(imageView.getContext()).load(imagePath).apply(mOptions).into(imageView);
        }

        @Override
        public void loadPreImage(ImageView imageView, String imagePath) {
            //大图加载
            Glide.with(imageView.getContext()).load(imagePath).apply(mPreOptions).into(imageView);

        }

        @Override
        public void clearMemoryCache() {
            //清理缓存
            Glide.get(WriteActivity.this).clearMemory();
        }
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CODE_CHOOSE:
                if (resultCode == RESULT_OK) {
                    isAttachmentInit = true;
                    initViewStub(data);
                }
                break;

            case REQUEST_CODE_CONTACT:
                if(resultCode==RESULT_OK){
                    String address=data.getStringExtra("respond");
//                    Toast.makeText(this,"地址"+address,Toast.LENGTH_SHORT).show();
                    et_receiver.setText(address);
                }

        }
    }

    //判断是否需要显示添加附件界面
    private void showAttachment() {
        if (isShow) {
            vs_add_attachment.setVisibility(View.GONE);
        } else {
            vs_add_attachment.setVisibility(View.VISIBLE);
        }
    }


    //初始化添加附件ViewStub
    private ImageView imageAdd;

    private void initViewStub(Intent data) {
        Log.d("AAA","initViewStub");

       View view= vs_add_attachment.inflate();
       imageAdd=view.findViewById(R.id.iv_add_attachment_vs);

        /*List<Uri> mSelected = Matisse.obtainResult(data);
        attachmentUri = mSelected.get(0);*/
        if(data!=null){
            List<String> imagePaths = data.getStringArrayListExtra(ImagePicker.EXTRA_SELECT_IMAGES);
            if(!imagePaths.isEmpty()) {
                String imgPath=imagePaths.get(0);
                Log.d("AAA","选择的图片路径："+attachmentPath);

                attachmentPath = imagePaths.get(0);
                Log.d("AAA",attachmentPath);
                //解决拍摄的照片会旋转的问题
                int degree= BitmapUtils.getBitmapDegree(attachmentPath);
                Bitmap headBmp= ImageUtil.getBitmapFromFile(attachmentPath);
                headBmp=BitmapUtils.rotaingImageView(degree,headBmp);

                Glide.with(this).load(headBmp).into(imageAdd);
            }
        }

    }



    //点击左上角取消按钮弹窗
    public void showCancelPopup() {
        new XPopup.Builder(this)
                .asConfirm("离开写邮件",
                        "已填写的邮件内容将丢失，或保存至草稿箱",
                        "取消", "保存草稿",
                        this::saveDraft,
                        this::finish,
                        false)
                .bindLayout(R.layout.leave_popup)
                .show();
    }

    private void saveDraft(){
        final BasePopupView loadingPopup = new XPopup.Builder(this)
                .asLoading("保存草稿中")
                .show();
        loadingPopup.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });

        String address = et_receiver.getText().toString();
        String subject = et_subject.getText().toString();
        String content = et_content.getText().toString();

        DraftInfo draftInfo= new DraftInfo(address, subject, content);
        draftInfo.save(new SaveListener<String>() {
            @Override
            public void done(String s, BmobException e) {
                loadingPopup.dismiss();
                if (e == null) {
                    Toast.makeText(WriteActivity.this,
                            "保存草稿成功", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(WriteActivity.this,
                            "保存草稿失败", Toast.LENGTH_LONG).show();
                }
            }
        });
    }


//    private void saveSent(){
//        DBUtil dbUtil=new DBUtil(this);
//        SQLiteDatabase db=dbUtil.getWritableDatabase();
//        ContentValues values=new ContentValues();
//        values.put("receiver",et_receiver.getText().toString());
//        values.put("subject",et_subject.getText().toString());
//        values.put("content",et_content.getText().toString());
//        if(!TextUtils.isEmpty(attachmentPath)){
//            values.put("path",attachmentPath);
//        }else{
//            values.put("path","");
//        }
//        Date now=new Date();
//        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
//        String date=formatter.format(now);
//        values.put("date",date);
//        db.insert("sent",null,values);
//    }


    private void saveSent(){
        String receiver=et_receiver.getText().toString();
        String subject=et_subject.getText().toString();
        String content=et_content.getText().toString();

        SentInfo sentInfo=new SentInfo(receiver,subject,content);
        if(!TextUtils.isEmpty(attachmentPath)){
            upLoadFile(sentInfo);
        }else{
            insertObject(sentInfo);
        }


    }

    private void insertObject(SentInfo sentInfo) {
        sentInfo.save(new SaveListener<String>() {
            @Override
            public void done(String s, BmobException e) {
                if(e==null){
                    Toast.makeText(WriteActivity.this,
                            "上传成功", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(WriteActivity.this,
                            "上传失败", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void upLoadFile(SentInfo sentInfo) {
        BmobFile bmobFile=new BmobFile(new File(attachmentPath));
        bmobFile.uploadblock(new UploadFileListener() {
            @Override
            public void done(BmobException e) {
                sentInfo.setAttachment(bmobFile);
                insertObject(sentInfo);
            }
        });
    }


//    /*
//压缩路径下的文件
// */
//    private File imageFactory(String picPath){
//        BitmapFactory.Options o=new BitmapFactory.Options();
//        Bitmap bitmap=BitmapFactory.decodeFile(picPath, o);
//        bitmap=Bitmap.createScaledBitmap(bitmap, 400, 400, false);
//        File root= getExternalCacheDir();
//        File pic=new File(root,"test.jpg");
//        try {
//            FileOutputStream fos=new FileOutputStream(pic);
//            bitmap.compress(Bitmap.CompressFormat.JPEG,50,fos);
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }
//        return pic;
//    }

}
