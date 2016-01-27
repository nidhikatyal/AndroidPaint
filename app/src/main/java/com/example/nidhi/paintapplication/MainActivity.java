package com.example.nidhi.paintapplication;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageButton;
import android.app.Dialog;
import android.view.View.OnClickListener;
import android.widget.Toast;

import java.util.UUID;
//import the 3rd party library for color picker
import yuku.ambilwarna.AmbilWarnaDialog;

public class MainActivity extends AppCompatActivity{
    private DrawingView mDrawView;
    private ImageButton mNewBtn;
    private ImageButton mDrawBtn;
    private ImageButton mColorSelectorBtn;
    private ImageButton mEraseBtn;
    private ImageButton mSaveBtn;

    //three dimensional values for brush size
    private float mSmallBrush, mMediumBrush, mLargeBrush;

    //default paint color
    int mColor = Color.BLUE;

    private Bundle mScreenStates;
    private static final String KEY_SCREEN_STATES = "key_screen_states";
    private static final String KEY_SAVE_DIALOG = "key_save_dialog";
    private static final String KEY_LAST_SELECTED_COLOR = "key_last_selected_color";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mScreenStates = (savedInstanceState != null)
                ? savedInstanceState.getBundle(KEY_SCREEN_STATES)
                : new Bundle();

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //retrieve the 3 brush sizes
        mSmallBrush = getResources().getInteger(R.integer.small_size);
        mMediumBrush = getResources().getInteger(R.integer.medium_size);
        mLargeBrush = getResources().getInteger(R.integer.large_size);

        //retrieve the custom drawing view that is added to the layout
        mDrawView = (DrawingView)findViewById(R.id.drawing);
        //default the brush size
        mDrawView.setBrushSize(mMediumBrush);
        //default color for drawing
        displayColor(mColor);

        //retrieve the view for creating new drawing
        mNewBtn = (ImageButton) findViewById(R.id.new_btn);
        mNewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createNewDrawing();
            }
        });

        //retrieve the paint button from the layout
        mDrawBtn = (ImageButton)findViewById(R.id.draw_btn);
        mDrawBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                openBrushSelection();
            }
        });

        //retrieve the color selector button from layout
        mColorSelectorBtn = (ImageButton)findViewById(R.id.color_btn);
        mColorSelectorBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openColorPalette(false);
            }
        });

        //retrieve the erase button from layout
        mEraseBtn = (ImageButton)findViewById(R.id.erase_btn);
        mEraseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openEraserSelection();
            }
        });

        //retrieve the save button from the layout
        mSaveBtn = (ImageButton)findViewById(R.id.save_btn);
        mSaveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveDrawing();
            }
        });

        int currentOrientation = getResources().getConfiguration().orientation;
        if (currentOrientation == Configuration.ORIENTATION_LANDSCAPE) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
        }
        else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mScreenStates.putInt(KEY_LAST_SELECTED_COLOR, mColor);
        outState.putBundle(KEY_SCREEN_STATES, mScreenStates);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mScreenStates = savedInstanceState.getBundle(KEY_SCREEN_STATES);
        if (mScreenStates != null) {
            if (mScreenStates.getBoolean(KEY_SAVE_DIALOG, false)) {
                saveDrawing();
            }
        }
        mColor = mScreenStates.getInt(KEY_LAST_SELECTED_COLOR);
    }


    public void createNewDrawing(){
        AlertDialog.Builder newDialog = new AlertDialog.Builder(this);
        newDialog.setTitle(getResources().getString(R.string.create_new_drawing));
        newDialog.setMessage(getResources().getString(R.string.create_new_prompt));
        newDialog.setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog, int which){
                mDrawView.startNew();
                dialog.dismiss();
            }
        });
        newDialog.setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog, int which){
                dialog.cancel();
            }
        });
        newDialog.show();
    }

    public void openBrushSelection(){
        //draw button clicked, present user with a dialog to select the brush size
        final Dialog brushDialog = new Dialog(this);
        brushDialog.setTitle(getResources().getString(R.string.brushsize));
        brushDialog.setContentView(R.layout.brush_chooser);

        displayColor(mColor);

        //listen for clicks on three brush size buttons
        final ImageButton smallBtn = (ImageButton)brushDialog.findViewById(R.id.small_brush);
        smallBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawView.setBrushSize(mSmallBrush);
                mDrawView.setLastBrushSize(mSmallBrush);
                mDrawView.setErase(false);
                brushDialog.dismiss();
            }
        });

        final ImageButton mediumBtn = (ImageButton)brushDialog.findViewById(R.id.medium_brush);
        mediumBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawView.setBrushSize(mMediumBrush);
                mDrawView.setLastBrushSize(mMediumBrush);
                mDrawView.setErase(false);
                brushDialog.dismiss();
            }
        });

        final ImageButton largeBtn = (ImageButton)brushDialog.findViewById(R.id.large_brush);
        largeBtn.setOnClickListener(new OnClickListener(){
            @Override
            public void onClick(View v) {
                mDrawView.setBrushSize(mLargeBrush);
                mDrawView.setLastBrushSize(mLargeBrush);
                mDrawView.setErase(false);
                brushDialog.dismiss();
            }
        });
        brushDialog.show();
    }

    public void openColorPalette(boolean supportsAlpha) {
        AmbilWarnaDialog dialog = new AmbilWarnaDialog(this, mColor, supportsAlpha, new AmbilWarnaDialog.OnAmbilWarnaListener() {
            @Override
            public void onOk(AmbilWarnaDialog dialog, int color) {
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.ok), Toast.LENGTH_SHORT).show();
                MainActivity.this.mColor = color;
                displayColor(color);
            }

            @Override
            public void onCancel(AmbilWarnaDialog dialog) {
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.cancel), Toast.LENGTH_SHORT).show();
            }
        });
        dialog.show();
    }

    public void openEraserSelection(){
        //switch to erase - choose size
        final Dialog brushDialog = new Dialog(this);
        brushDialog.setTitle(getResources().getString(R.string.erasersize));
        brushDialog.setContentView(R.layout.brush_chooser);

        displayColor(0xffffffff);//set white color
        final ImageButton smallBtn = (ImageButton)brushDialog.findViewById(R.id.small_brush);
        smallBtn.setOnClickListener(new OnClickListener(){
            @Override
            public void onClick(View v) {
                mDrawView.setErase(true);
                mDrawView.setBrushSize(mSmallBrush);
                brushDialog.dismiss();
            }
        });
        final ImageButton mediumBtn = (ImageButton)brushDialog.findViewById(R.id.medium_brush);
        mediumBtn.setOnClickListener(new OnClickListener(){
            @Override
            public void onClick(View v) {
                mDrawView.setErase(true);
                mDrawView.setBrushSize(mMediumBrush);
                brushDialog.dismiss();
            }
        });
        final ImageButton largeBtn = (ImageButton)brushDialog.findViewById(R.id.large_brush);
        largeBtn.setOnClickListener(new OnClickListener(){
            @Override
            public void onClick(View v) {
                mDrawView.setErase(true);
                mDrawView.setBrushSize(mLargeBrush);
                brushDialog.dismiss();
            }
        });
        brushDialog.show();
    }

    public void saveDrawing(){
        AlertDialog.Builder saveDialog = new AlertDialog.Builder(this);
        saveDialog.setTitle(getResources().getString(R.string.savedrawing));
        saveDialog.setMessage(getResources().getString(R.string.savedrawingtogallery));
        saveDialog.setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                //save drawing
                mDrawView.setDrawingCacheEnabled(true);
                String imgSaved = MediaStore.Images.Media.insertImage(
                        getContentResolver(), mDrawView.getDrawingCache(),
                        UUID.randomUUID().toString() + ".png", "drawing");
                if (imgSaved != null) {
                    Toast savedToast = Toast.makeText(getApplicationContext(),
                            getResources().getString(R.string.saved), Toast.LENGTH_SHORT);
                    savedToast.show();
                } else {
                    Toast unsavedToast = Toast.makeText(getApplicationContext(),
                            getResources().getString(R.string.notsaved), Toast.LENGTH_SHORT);
                    unsavedToast.show();
                }
                mDrawView.destroyDrawingCache();
            }
        });
        saveDialog.setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        saveDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                mScreenStates.putBoolean(KEY_SAVE_DIALOG, false);
            }
        });
        saveDialog.show();
        mScreenStates.putBoolean(KEY_SAVE_DIALOG, true);
    }

    public void displayColor(int color) {
        //sets the color for the drawing
        mDrawView.setColor(color);
   }
}
