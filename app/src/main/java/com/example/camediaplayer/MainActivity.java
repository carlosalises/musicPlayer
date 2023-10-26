package com.example.camediaplayer;

import static java.lang.Thread.sleep;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    private MediaPlayer mediaPlayer;
    private AudioManager _audioManager;
    private AdaptadorCanciones adaptador;
    private Cancion cancion;
    private int _pauseMoment;
    private boolean _isPaused;
    private boolean _isStarted;
    private boolean _restartSong;
    private boolean runningThread;
    private boolean _iniciateButtons = false;
    private SeekBar _barraCancion;
    private Thread _updateSeekBar;
    int _currentSong;
    private ArrayList<Cancion> _listSongsMediaPlayer;
    static final int REQUEST_VIDEO_CAPTURE = 1;
    private final Cancion[] canciones= new Cancion[] {new Cancion(R.drawable.pasarmelobien,"VOY A PASARMELO BIEN","HOMBRES G",R.raw.voyapasarmelobien), new Cancion(R.drawable.quieroestar,"DONDE QUIERO ESTAR","QUEVEDO", R.raw.dondequieroestar),new Cancion(R.drawable.pepas,"PEPAS","FARRUKO", R.raw.pepas), new Cancion(R.drawable.mananaserabonito,"PER TU","KAROL G FT QUEVEDO", R.raw.perotu)};


    //RECORDER
    private MediaRecorder _mediaRecorder;
    private String outputFile = null;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        ImageView _imageSong = (ImageView) findViewById(R.id.imgSong);
        //_imageSong.setImageResource(R.drawable.iconoprincipal);
        TextView _tituloSong = (TextView) findViewById(R.id.titleSong);
        TextView _nameArtist = (TextView) findViewById(R.id.nameArtist);

        //Buttons
        ImageButton playButton = (ImageButton) findViewById(R.id.playButton);
        ImageButton reiniciarButton = (ImageButton) findViewById(R.id.reiniciarButton);
        ImageButton bibliotecaButton = (ImageButton) findViewById(R.id.bibliotecaButton);
        ImageButton videoButton = (ImageButton) findViewById(R.id.videoButton);
        ImageButton recorderButton = (ImageButton) findViewById(R.id.recorderButton);

        //Barra Cancion

        _barraCancion = (SeekBar) findViewById(R.id.barraCancion);

        // VOLUMEN
        SeekBar _barraVolumen = (SeekBar) findViewById(R.id.volumeBar);

        _audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        //MAX VOLUME
        int _maxVolume = _audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        //CURRENT VOLUME
        int _currentVolume = _audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);

        _barraVolumen.setMax(_maxVolume);
        _barraVolumen.setProgress(_currentVolume);
        //

        playButton.setVisibility(View.INVISIBLE);
        reiniciarButton.setVisibility(View.INVISIBLE);


        _listSongsMediaPlayer = new ArrayList<Cancion>(Arrays.asList(canciones));

        adaptador = new AdaptadorCanciones(this);
        ListView _lstSongs = (ListView) findViewById(R.id.lstSongs);
        _lstSongs.setAdapter(adaptador);

        _restartSong = false;

        //GetLibrarySongs();

        _lstSongs.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                // TODO Auto-generated method stub

                ClearMediaPlayer();

                ResetSeekBar();
                ActivateButtons(playButton, reiniciarButton);

                cancion = _listSongsMediaPlayer.get(arg2);
                _currentSong = _listSongsMediaPlayer.indexOf(arg2);

                _imageSong.setImageResource(cancion.getImage());
                _tituloSong.setText(cancion.getTitulo());
                _nameArtist.setText(cancion.getArtista());

                playButton.setImageResource(R.drawable.ic_baseline_pause_24);
                StartSong();



            }
        });

        View.OnClickListener buttonsMediaPlayer = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case(R.id.playButton):
                        if(mediaPlayer != null){
                            if(_isPaused) {
                                playButton.setImageResource(R.drawable.ic_baseline_pause_24);
                                mediaPlayer.seekTo(_pauseMoment);
                                _isPaused = false;
                                mediaPlayer.start();
                            }else  {
                                playButton.setImageResource(R.drawable.ic_baseline_play_arrow_24);
                                _isPaused = true;
                                mediaPlayer.pause();
                                _pauseMoment = mediaPlayer.getCurrentPosition();
                            }
                        }
                        break;
                    case(R.id.reiniciarButton):
                        if(_isStarted) {
                            playButton.setImageResource(R.drawable.ic_baseline_pause_24);
                            ResetSong();
                            mediaPlayer.start();
                            _restartSong = true;
                            if(_isPaused){
                                _isPaused = false;
                            }
                        }
                        break;
                    case(R.id.bibliotecaButton):
                        StartBibliotecaActivity();
                        break;
                    case(R.id.videoButton):
                        ClearMediaPlayer();
                        StartCameraActivity();
                        ResetSeekBar();
                        break;
                    case (R.id.recorderButton):
                        ClearMediaPlayer();
                        RecorderFunction();
                        ResetSeekBar();
                        break;
                    default:
                        break;
                }
            }
        };
        playButton.setOnClickListener(buttonsMediaPlayer);
        reiniciarButton.setOnClickListener(buttonsMediaPlayer);
        bibliotecaButton.setOnClickListener(buttonsMediaPlayer);
        videoButton.setOnClickListener(buttonsMediaPlayer);
        recorderButton.setOnClickListener(buttonsMediaPlayer);


        _barraCancion.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                if(b){
                    mediaPlayer.seekTo(progress);
                }

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                mediaPlayer.pause();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mediaPlayer.start();
                playButton.setImageResource(R.drawable.ic_baseline_pause_24);
                if(_isPaused == true)
                    _isPaused = false;
            }
        });

        _barraVolumen.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                _audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress,0);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });


    }

    // MEDIAPLAYER FUNCTIONS

    private void StartSong() {
        mediaPlayer = MediaPlayer.create(getApplicationContext(), cancion.getUri());
        _updateSeekBar = ExecuteSeekBarThread();
        _updateSeekBar.start();
        _isStarted = true;
        _isPaused = false;
        _restartSong = false;
    }

    private void ClearMediaPlayer() {
        if(mediaPlayer != null){
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
            if (_updateSeekBar.isAlive()){
                _updateSeekBar.interrupt();
            }

        }
    }

    private void ActivateButtons(ImageButton play, ImageButton reinciar) {

        if(_iniciateButtons == false) {
            play.setVisibility(View.VISIBLE);
            reinciar.setVisibility(View.VISIBLE);
            _iniciateButtons = true;
        }

    }

    private void StartBibliotecaActivity() {
        Intent biblioteca = new Intent(this, Biblioteca.class);
        startActivity(biblioteca);
        finish();
    }

    private void ResetSong() {
        mediaPlayer.seekTo(0);
    }
    private void ResetSeekBar() {
        _barraCancion.setProgress(0);
    }

    private void StopThread(Thread thread) {
        if(thread.isAlive()){
            thread.interrupt();
        }
    }



    private void GetLibrarySongs() {
        Intent intent = getIntent();
        if (intent.hasExtra("lista_canciones")) {
            ArrayList<Cancion> songsLibrary = (ArrayList<Cancion>) intent.getSerializableExtra("lista_canciones");
            for(int x = 0; x < songsLibrary.size(); x++) {
                _listSongsMediaPlayer.add(songsLibrary.get(x));
            }
        }
    }

    private Thread ExecuteSeekBarThread() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                runningThread = true;
                mediaPlayer.start();
                _barraCancion.setMax(mediaPlayer.getDuration());
                int totalDuration = mediaPlayer.getDuration();
                int currentPosition = 0;

                while (mediaPlayer != null) {
                    if(currentPosition < totalDuration){
                        try {
                            Thread.sleep(500);
                            currentPosition = mediaPlayer.getCurrentPosition();
                            _barraCancion.setProgress(currentPosition);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
                runningThread = false;
            }
        });
        return thread;
    }

    //CAMERA
    private void StartCameraActivity() {
        Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        if (takeVideoIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takeVideoIntent, REQUEST_VIDEO_CAPTURE);
        }
    }

    //RECORDER
    private void RecorderFunction(){
        if(_mediaRecorder == null){
            outputFile = Environment.getExternalStorageDirectory().getAbsolutePath() + "/recording.mp3";
            _mediaRecorder = new MediaRecorder();
            _mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            _mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            _mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
            _mediaRecorder.setOutputFile(outputFile);

            try{
                _mediaRecorder.prepare();
                _mediaRecorder.start();
            } catch (IOException e){
            }

            Toast.makeText(getApplicationContext(), "Grabando...", Toast.LENGTH_SHORT).show();
        } else {
            _mediaRecorder.stop();
            _mediaRecorder.release();
            _mediaRecorder = null;
            Toast.makeText(getApplicationContext(), "Grabación finalizada", Toast.LENGTH_SHORT).show();
        }
    }

    class AdaptadorCanciones extends ArrayAdapter  {

        Activity context;
        public AdaptadorCanciones(Activity context) {
            super(context, R.layout.listitemcanciones, _listSongsMediaPlayer);
            this.context = (Activity) context;
        }


        // GetView s'executa per cada element de l'array de dades i el que fa
        // es "inflar" el layout del XML que hem creat

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub
            LayoutInflater inflater = context.getLayoutInflater();
            View item = inflater.inflate(R.layout.listitemcanciones, null);

            TextView _nameSong = (TextView) item.findViewById(R.id.nameSong);
            _nameSong.setText(_listSongsMediaPlayer.get(position).getTitulo().toString());

            TextView _artista = (TextView) item.findViewById(R.id.artista);
            _artista.setText(_listSongsMediaPlayer.get(position).getArtista().toString());

            ImageView _songImage = (ImageView) item.findViewById(R.id.imgSong);
            _songImage.setImageResource(_listSongsMediaPlayer.get(position).getImage());
            return (item);
        }

    }

}