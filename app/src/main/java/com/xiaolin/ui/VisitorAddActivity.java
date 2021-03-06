package com.xiaolin.ui;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.xiaolin.R;
import com.xiaolin.app.Constants;
import com.xiaolin.dialog.CameraChooseDialog;
import com.xiaolin.dialog.DateChooseWheelViewDialog;
import com.xiaolin.library.PermissionListener;
import com.xiaolin.library.PermissionsUtil;
import com.xiaolin.presenter.VisitorPresenterImpl;
import com.xiaolin.presenter.ipresenter.IVisitorPresenter;
import com.xiaolin.ui.base.BaseActivity;
import com.xiaolin.ui.iview.ICommonView;
import com.xiaolin.utils.CameraGalleryUtils;
import com.xiaolin.utils.DebugUtil;
import com.xiaolin.utils.GlideCircleTransform;
import com.xiaolin.utils.SPUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 添加访客
 */

public class VisitorAddActivity extends BaseActivity implements ICommonView {
    private static final String TAG = "visitor";
    //topbar
    @BindView(R.id.tv_title)
    TextView tv_title;

    @BindView(R.id.tv_right)
    TextView tv_right;

    @BindView(R.id.layout_back)
    RelativeLayout layout_back;

    //图片
    @BindView(R.id.pic_img)
    ImageView pic_img;


    //姓名
    @BindView(R.id.tv_name)
    EditText tv_name;

    //电话
    @BindView(R.id.tv_phone)
    EditText tv_phone;

    //开始时间
    @BindView(R.id.tv_startTime)
    TextView tv_startTime;

    //结束时间
    @BindView(R.id.tv_endTime)
    TextView tv_endTime;

    //受访者
    @BindView(R.id.tv_visitorTo)
    TextView tv_visitorTo;

    //目的
    @BindView(R.id.tv_purpose)
    EditText tv_purpose;

    //公司
    @BindView(R.id.tv_company)
    EditText tv_company;

    //备注
    @BindView(R.id.tv_remark)
    EditText tv_remark;

    //提交
    @BindView(R.id.btn_visitor)
    Button btn_visitor;

    //变量


    CameraChooseDialog dialog;
    Uri uri;
    File file;
    int width = -1;//设定保存图片的宽高,经测试，图片宽度过大比如屏幕宽当width，保存图片app会崩，目前没解决大图保存的问题
    IVisitorPresenter visitorPresenter;

    String startTime;
    String endTime;
    String name;
    String phone;
    String aim;
    String company;
    String remark;
    String visitorTo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_add_visitor);
        initMyView();

    }

    private void initMyView() {
        ButterKnife.bind(this);
        //        width = Utils.getWindowWidth(VisitorAddActivity.this) / 2;//以手机屏宽截图
        width = 300;//以手机屏宽截图
        tv_title.setText(R.string.visitor_add_title);
        tv_right.setText("");
        visitorTo = SPUtils.getString(Constants.EMPLOYEENAME);
        tv_visitorTo.setText(visitorTo);

        visitorPresenter = new VisitorPresenterImpl(VisitorAddActivity.this, this);
    }

    private void getValues() {
        name = tv_name.getText().toString();
        phone = tv_phone.getText().toString();

        aim = tv_purpose.getText().toString();
        company = tv_company.getText().toString();
        remark = tv_remark.getText().toString();
    }

    private String setJsonStr() {
        JSONObject js = new JSONObject();
        try {
            js.put("RecordID", "");
            js.put("VisitorID", "");
            js.put("VisitorName", name);
            js.put("PhoneNumber", phone);
            js.put("RespondentID", SPUtils.getString(Constants.EMPLOYEE_ID));
            js.put("StoreID", SPUtils.getString(Constants.STORE_ID));
            js.put("Aim", aim);
            js.put("Affilication", company);
            js.put("ArrivalTimePlan", startTime);
            js.put("LeaveTimePlan", endTime);
            js.put("Remark", remark);
            js.put("isVip", true);//
            js.put("WelcomeWord", "");
            js.put("GroupId", "");
            js.put("OperatorName", SPUtils.getString(Constants.USRENAME));


        } catch (JSONException e) {
            e.printStackTrace();
        }
        return js.toString();
    }

    private boolean isEmpoty() {
        if (file == null) {
            DebugUtil.ToastShort(VisitorAddActivity.this, "图片为空，请检查！");
            return true;
        }
        if (TextUtils.isEmpty(name)) {
            DebugUtil.ToastShort(VisitorAddActivity.this, "姓名不能为空！");
            return true;
        }
        if (TextUtils.isEmpty(startTime) || TextUtils.isEmpty(startTime)) {
            DebugUtil.ToastShort(VisitorAddActivity.this, "时间不能为空！");
            return true;
        }
        if (TextUtils.isEmpty(phone)) {
            DebugUtil.ToastShort(VisitorAddActivity.this, "电话不能为空！");
            return true;
        }

        if (TextUtils.isEmpty(visitorTo)) {
            DebugUtil.ToastShort(VisitorAddActivity.this, "受访者不能为空！");
            return true;
        }
        if (TextUtils.isEmpty(company)) {
            DebugUtil.ToastShort(VisitorAddActivity.this, "所属单位不能为空！");
            return true;
        }

        return false;
    }

    /**
     * 多控件 监听
     *
     * @param view
     */
    @OnClick({R.id.btn_visitor, R.id.layout_start, R.id.layout_end, R.id.pic_img, R.id.layout_back})
    public void multClick(View view) {
        switch (view.getId()) {
            case R.id.btn_visitor://提交
                getValues();
                if (!isEmpoty()) {
                    String str = setJsonStr();
                    visitorPresenter.addVisitor(str, file);
                }
                break;
            case R.id.pic_img://相机
                takepic_permission();
                break;
            case R.id.layout_start://开始时间
                startTime();
                break;
            case R.id.layout_end://结束时间
                endTime();
                break;

            case R.id.layout_back://退出
                this.finish();
                break;
        }

    }

    /**
     * 开始时间
     */
    private void startTime() {
        DateChooseWheelViewDialog endDateChooseDialog = new DateChooseWheelViewDialog(VisitorAddActivity.this,
                new DateChooseWheelViewDialog.DateChooseInterface() {
                    @Override
                    public void getDateTime(String time, boolean longTimeChecked) {
                        endTime = time;
                        tv_startTime.setText(time);
                    }
                });
        //        endDateChooseDialog.setTimePickerGone(true);
        endDateChooseDialog.setDateDialogTitle("开始时间");
        endDateChooseDialog.showDateChooseDialog();
    }

    /**
     * 结束时间
     */

    private void endTime() {
        DateChooseWheelViewDialog endDateChooseDialog = new DateChooseWheelViewDialog(VisitorAddActivity.this,
                new DateChooseWheelViewDialog.DateChooseInterface() {
                    @Override
                    public void getDateTime(String time, boolean longTimeChecked) {
                        startTime = time;
                        tv_endTime.setText(time);
                    }
                });
        //        endDateChooseDialog.setTimePickerGone(true);
        endDateChooseDialog.setDateDialogTitle("结束时间");
        endDateChooseDialog.showDateChooseDialog();
    }

    /**
     * 拍照前权限设置
     */
    private void takepic_permission() {
        //相机+读写权限组 提示
        if (PermissionsUtil.hasPermission(this, Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)) {//ACCESS_FINE_LOCATION ACCESS_COARSE_LOCATION这两个是一组，用一个判断就够了
            // TODO: 2017/8/16 相机功能方式1需要改bug，方式2需要优化
            //            takepic();//方式1
            CameraGalleryUtils.showImagePickDialog(VisitorAddActivity.this);//方式2
        } else {
            //第一次使用该权限调用
            PermissionsUtil.requestPermission(this
                    , new PermissionListener() {
                        @Override
                        public void permissionGranted(@NonNull String[] permissions) {
                            //允许使用就跳转界面
                            //                            takepic();//方式1
                            CameraGalleryUtils.showImagePickDialog(VisitorAddActivity.this);//方式2
                        }

                        @Override
                        public void permissionDenied(@NonNull String[] permissions) {
                            DebugUtil.toastLong(VisitorAddActivity.this, "该功能不可用");
                        }
                    }
                    , Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE);
        }

    }

    /**
     * 使用CameraGalleryUtils相机 方法2
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case CameraGalleryUtils.REQUEST_CODE_FROM_ALBUM: {

                if (resultCode == RESULT_CANCELED) {   //取消操作
                    return;
                }

                Uri imageUri = data.getData();
                CameraGalleryUtils.copyImageUri(this, imageUri);
                CameraGalleryUtils.cropImageUri(this, CameraGalleryUtils.getCurrentUri(), 200, 200);
                break;
            }
            case CameraGalleryUtils.REQUEST_CODE_FROM_CAMERA: {

                if (resultCode == RESULT_CANCELED) {     //取消操作
                    CameraGalleryUtils.deleteImageUri(this, CameraGalleryUtils.getCurrentUri());   //删除Uri
                }

                CameraGalleryUtils.cropImageUri(this, CameraGalleryUtils.getCurrentUri(), 200, 200);
                break;
            }
            case CameraGalleryUtils.REQUEST_CODE_CROP: {

                if (resultCode == RESULT_CANCELED) {     //取消操作
                    return;
                }

                Uri imageUri = CameraGalleryUtils.getCurrentUri();
                if (imageUri != null) {
                    //                    pic_img.setImageURI(imageUri);
                    file = uri2File(imageUri);
                    Glide.with(VisitorAddActivity.this)
                            .load(file)
                            .diskCacheStrategy(DiskCacheStrategy.RESULT)
                            .placeholder(ContextCompat.getDrawable(VisitorAddActivity.this, R.mipmap.default_photo))
                            .error(ContextCompat.getDrawable(VisitorAddActivity.this, R.mipmap.default_photo))
                            .transform(new GlideCircleTransform(VisitorAddActivity.this))//自定义圆形图片
                            .into(pic_img);
                }
                break;
            }
            default:
                break;
        }
    }

    private File uri2File(Uri uri) {
        try {
            file = new File(new URI(uri.toString()));
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return file;
    }

    /**
     * 拍照 方式1
     */
    //    private void takepic() {
    //        if (width <= 0) {
    //            return;
    //        }
    //
    //        //选择图片方式
    //        dialog = new CameraChooseDialog(VisitorAddActivity.this, new CameraChooseDialog.ClickCallback() {
    //            @Override
    //            public void PhotoCallback() {
    //                DebugUtil.d(TAG, "相机");
    //                dialog.dismiss();
    //                Intent intent = EditPictureUtil.getCameraIntent(VisitorAddActivity.this);
    //                startActivityForResult(intent, CAMERA_REQUEST_CODE);
    //            }
    //
    //            @Override
    //            public void galleryCallback() {
    //                DebugUtil.d(TAG, "相册");
    //                dialog.dismiss();
    //
    //                Intent intent = EditPictureUtil.getGalleryIntent(width, width, VisitorAddActivity.this);
    //                startActivityForResult(intent, GALLERY_REQUEST_CODE);
    //            }
    //        });
    //        dialog.show();
    //
    //    }

    private static final int CAMERA_REQUEST_CODE = 1;
    private static final int GALLERY_REQUEST_CODE = 3;
    private static final int CAMERA_SHOW = 2;
    private static final int GALLERY_SHOW = 4;

    /**
     * 使用EditPictureUtil 方法1
     */

    //    @Override
    //    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    //        super.onActivityResult(requestCode, resultCode, data);
    //
    //        if (resultCode == Activity.RESULT_OK) {
    //            switch (requestCode) {
    //
    //                case CAMERA_REQUEST_CODE:
    //
    //                    DebugUtil.d("GGG", " camera剪裁");
    //
    //                    Uri uri = data.getData();
    //                    // 启动修剪相片功能
    //                    Intent intent = EditPictureUtil.getCameraCropIntent(uri//EditPictureUtil.getCamreraOrgFileUri(VisitorAddActivity.this)
    //                            , width
    //                            , width
    //                            , EditPictureUtil.createCameraCropImageFile(VisitorAddActivity.this));
    //                    startActivityForResult(intent, CAMERA_SHOW);//剪裁完成
    //                    break;
    //
    //                case GALLERY_REQUEST_CODE:
    //
    //                    DebugUtil.d("GGG", " gallery剪裁");
    //                    // 启动修剪相片功能
    //                    Intent intent2 = EditPictureUtil.getGalleryCropIntent(EditPictureUtil.getGalleryOrgFileUri(VisitorAddActivity.this)
    //                            , width
    //                            , width
    //                            , EditPictureUtil.getGalleryCropImageFile(VisitorAddActivity.this));
    //                    startActivityForResult(intent2, GALLERY_SHOW);//剪裁完成
    //                    break;
    //
    //                case CAMERA_SHOW:
    //
    //                    DebugUtil.d("GGG", " camera显示");
    //                    uri = EditPictureUtil.getCameraCropImageFileUri(VisitorAddActivity.this);
    //                    file = EditPictureUtil.getCameraCropImageFile(VisitorAddActivity.this);
    //                    DebugUtil.d("GGG", "显示camera路径：" + uri.toString());
    //
    //                    //显示图片
    //                    //                Bitmap bitmap = EditPictureUtil.getBitmapFromUri(VisitorAddActivity.this, uri);
    //                    //                pic_img.setImageBitmap(bitmap);
    //                    if (file != null) {
    //                        Glide.with(VisitorAddActivity.this)
    //                                .load(file)
    //                                .diskCacheStrategy(DiskCacheStrategy.RESULT)
    //                                .placeholder(ContextCompat.getDrawable(VisitorAddActivity.this, R.mipmap.default_photo))
    //                                .error(ContextCompat.getDrawable(VisitorAddActivity.this, R.mipmap.default_photo))
    //                                .transform(new GlideCircleTransform(VisitorAddActivity.this))//自定义圆形图片
    //                                .into(pic_img);
    //                    }
    //                    break;
    //
    //                case GALLERY_SHOW:
    //
    //                    DebugUtil.d("GGG", " gallery显示");
    //                    uri = EditPictureUtil.getGalleryCropImageFileUri(VisitorAddActivity.this);
    //                    file = EditPictureUtil.getGalleryCropImageFile(VisitorAddActivity.this);
    //                    DebugUtil.d("GGG", "显示gallery路径：" + uri.toString());
    //
    //                    //显示图片
    //                    //                Bitmap bitmap = EditPictureUtil.getBitmapFromUri(VisitorAddActivity.this, uri);
    //                    //                pic_img.setImageBitmap(bitmap);
    //                    if (file != null) {
    //                        Glide.with(VisitorAddActivity.this)
    //                                .load(file)
    //                                .diskCacheStrategy(DiskCacheStrategy.RESULT)
    //                                .placeholder(ContextCompat.getDrawable(VisitorAddActivity.this, R.mipmap.default_photo))
    //                                .error(ContextCompat.getDrawable(VisitorAddActivity.this, R.mipmap.default_photo))
    //                                .transform(new GlideCircleTransform(VisitorAddActivity.this))//自定义圆形图片
    //                                .into(pic_img);
    //                    }
    //                    break;
    //            }
    //        }
    //
    //    }
    @Override
    public void showProgress() {
        loadingDialog.show();
    }

    @Override
    public void hideProgress() {
        loadingDialog.dismiss();
    }

    @Override
    public void postSuccessShow(String str) {
        DebugUtil.ToastShort(VisitorAddActivity.this, "添加成功！");
        this.finish();
    }

    @Override
    public void postFaild(String msg, Exception e) {
        DebugUtil.ToastShort(VisitorAddActivity.this, msg);
    }
}
