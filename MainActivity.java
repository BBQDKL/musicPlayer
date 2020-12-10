package com.example.musicplayer;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private ListView listView;
    private MediaPlayer mediaPlayer;
    private SeekBar seekBar;
    private TextView playing;
    private int[] flag = {0};

    private int[] sf = {0};
    //歌曲次序
    private int[] musics = {R.raw.chengdu,R.raw.senlin,R.raw.yasugongshang,R.raw.zhiqingchun};
    private int[] index = {1,2,3,4};
    private String[] id = {"1","2","3","4"};
    //歌曲的ID

    private String[] songs = {"成都","森林狂想曲","雅俗共赏","致青春"};
    //歌曲名

    private String[] maker = {"赵雷","陶笛","许嵩","王菲"};
    //歌手姓名

    private int[] num = {0,0,0,0};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView = findViewById(R.id.song_list);
        //显示歌曲名称，以及歌手姓名

        playing = findViewById(R.id.song_currentplay);
        //当前正在播放的音乐

        registerForContextMenu(listView);
        //注册上下文菜单

        mediaPlayer=MediaPlayer.create(this,R.raw.chengdu);
        setListview(getAll());

        playing.setText("Now Playing: ");
        //显示文本
        sf[0]=1;
        //记录播放位置
        seekBar = findViewById(R.id.play_bar);
        //播放的进度条seekBar
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(fromUser){
                    mediaPlayer.seekTo(progress);
                }
            }


            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        seekBar.setMax(mediaPlayer.getDuration());
        new myThread().start();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                TextView songid = view.findViewById(R.id.song_id);
                String strid = songid.getText().toString();//得到正在播放的歌曲的ID
                int a = Integer.parseInt(strid);
                if(a == 2)
                {
                    mediaPlayer.reset();
                    mediaPlayer=MediaPlayer.create(MainActivity.this,musics[1]);
                    playing.setText("Now Playing: 森林狂想曲 2");
                    mediaPlayer.start();
                    seekBar.setMax(mediaPlayer.getDuration());
                    sf[0]=2;//记录
                }else if(a == 3)
                {
                    mediaPlayer.reset();
                    mediaPlayer=MediaPlayer.create(MainActivity.this,musics[2]);
                    playing.setText("Now Playing: 雅俗共赏 3");
                    mediaPlayer.start();
                    seekBar.setMax(mediaPlayer.getDuration());
                    sf[0]=3;
                }else if(a == 4)
                {
                    mediaPlayer.reset();
                    mediaPlayer=MediaPlayer.create(MainActivity.this,musics[3]);
                    playing.setText("Now Playing: 致青春 4");
                    mediaPlayer.start();
                    seekBar.setMax(mediaPlayer.getDuration());
                    sf[0]=4;
                }else
                {
                    mediaPlayer.reset();
                    mediaPlayer=MediaPlayer.create(MainActivity.this,musics[0]);
                    playing.setText("Now Playing: 成都 1 ");
                    mediaPlayer.start();
                    seekBar.setMax(mediaPlayer.getDuration());
                    sf[0]=1;
                }
            }
        });
        //上一首last，播放play，下一首next
        Button last = findViewById(R.id.play_last);
        Button play = findViewById(R.id.play_stop);
        Button next = findViewById(R.id.play_next);

        //播放方式，随机，单曲，顺序
        Spinner spinner = findViewById(R.id.play_mode);

        final ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(this,R.array.items,R.layout.support_simple_spinner_dropdown_item);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_item);;

        spinner.setAdapter(adapter1);
        spinner.setSelection(flag[0]);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String choice = (String)adapter1.getItem(i);
                switch (choice)
                {
                    case "Order":
                        autoplay();
                        break;
                    case "Random":
                        break;
                    case "Single":
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        //上一首
        last.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(sf[0]>1){
                    for(int i=sf[0]-2;i>=0;i--){
                        if(index[i]>0){
                            sf[0]=i+1;
                            mediaPlayer.reset();
                            mediaPlayer=MediaPlayer.create(MainActivity.this,musics[i]);
                            playing.setText("Now Playing："+songs[i]+sf[0]);
                            mediaPlayer.start();
                            seekBar.setMax(mediaPlayer.getDuration());
                            break;
                        }
                    }
                }else {
                    sf[0]=4;
                    mediaPlayer.reset();
                    mediaPlayer=MediaPlayer.create(MainActivity.this,musics[3]);
                    playing.setText("Now Playing: 4  致青春");
                    mediaPlayer.start();
                    seekBar.setMax(mediaPlayer.getDuration());
                }
            }
        });
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mediaPlayer!=null&&!mediaPlayer.isPlaying()){
                    mediaPlayer.start();
                }else{
                    mediaPlayer.pause();
                }
            }
        });


        //下一首
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(sf[0]<4){
                    for(int i=sf[0];i<4;i++){
                        if(index[i]>0){
                            sf[0]=i+1;
                            mediaPlayer.reset();
                            mediaPlayer=MediaPlayer.create(MainActivity.this,musics[i]);
                            playing.setText("Now Playing:"+sf[0]+songs[i]);
                            mediaPlayer.start();
                            seekBar.setMax(mediaPlayer.getDuration());
                            break;
                        }
                    }
                }else {
                    sf[0]=1;
                    mediaPlayer.reset();
                    mediaPlayer=MediaPlayer.create(MainActivity.this,R.raw.chengdu);
                    playing.setText("Now Playing: 1 成都");
                    mediaPlayer.start();
                    seekBar.setMax(mediaPlayer.getDuration());
                }
            }
        });
    }




    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menuoption,menu);
        return true;
    }




    public boolean onOptionsItemSelected(MenuItem item)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final View viewDialog = LayoutInflater.from(this).inflate(R.layout.helpdialog,null,false);
        builder.setTitle("Help")
                .setView(viewDialog)
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
        builder.create().show();
        return super.onOptionsItemSelected(item);
    }



    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        getMenuInflater().inflate(R.menu.contextoption, menu);
    }



    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info=null;
        View itemView=null;
        TextView textId=null;
        TextView text=null;
        switch (item.getItemId()){
            //删除
            case R.id.music_delete:
                info=(AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
                itemView=info.targetView;
                textId =(TextView)itemView.findViewById(R.id.song_id);
                text=(TextView)itemView.findViewById(R.id.song_name);
                final String strid= textId.getText().toString();
                final String t= text.getText().toString();
                final int a= Integer.parseInt(strid);
                new AlertDialog.Builder(this)
                        .setTitle("Delete Song").setMessage("Are You Sure?")
                        .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogInterface, int i) {
                                for(int j=0;j<index.length;j++){
                                    if(index[j]==a){
                                        index[j]=0;
                                    }
                                }
                                setListview(getAll());
                                num[a-1]=a;
                                TextView v=(TextView)findViewById(R.id.blank);
                                String str="";
                                for(int j=0;j<num.length;j++){
                                    if(num[j]==1){
                                        str=str+"   "+"1.成都";
                                    }else if(num[j]==2){
                                        str=str+"   "+"2.森林狂想曲";
                                    }else if(num[j]==3){
                                        str=str+"   "+"3.雅俗共赏";
                                    }else if(num[j]==4){
                                        str=str+"   "+"4.致青春";
                                    }
                                }
                                v.setText(str);
                            }
                        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                }).create().show();

                break;

        }
        return true;
    }
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
        }

    }
    public ArrayList<Map<String,Object>> getAll()
    {
        ArrayList<Map<String,Object>> list = new ArrayList<>();
        for(int i=0;i<index.length;i++) {
            if(index[i]>0){
                Map<String,Object> item=new HashMap<String,Object>();
                item.put("id", id[i]);
                item.put("songs", songs[i]);
                item.put("maker",maker[i]);
                list.add(item);
            }
        }
        return list;
    }
    public void autoplay(){
        if(sf[0]<8){
            for(int i=sf[0];i<8;i++){
                if(index[i]>0){
                    sf[0]=i+1;
                    mediaPlayer.reset();
                    mediaPlayer=MediaPlayer.create(MainActivity.this,musics[i]);
                    playing.setText("Now Playing: "+songs[i]);
                    mediaPlayer.start();
                    seekBar.setMax(mediaPlayer.getDuration());
                    break;
                }
            }
        }else {
            sf[0]=1;
            mediaPlayer.reset();
            mediaPlayer=MediaPlayer.create(MainActivity.this,R.raw.chengdu);
            playing.setText("Now Playing: 成都");
            mediaPlayer.start();
            seekBar.setMax(mediaPlayer.getDuration());
        }

    }
    private void setListview(ArrayList<Map<String, Object>> items) {
        SimpleAdapter adapter = new SimpleAdapter(this, items, R.layout.song_item,
                new String[]{"id", "songs", "maker"},
                new int[]{R.id.song_id, R.id.song_name, R.id.song_maker});
        listView.setAdapter(adapter);
    }
    class myThread extends Thread{
        @Override
        public void run()
        {
            super.run();
            while(seekBar.getProgress()<=seekBar.getMax())
            {
                seekBar.setProgress(mediaPlayer.getCurrentPosition());
            }
        }
    }
}