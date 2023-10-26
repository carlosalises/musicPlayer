package com.example.camediaplayer;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
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
import android.widget.TextView;
import android.widget.VideoView;

import java.util.ArrayList;

public class Biblioteca extends AppCompatActivity {

    private static final int PICK_IMAGE_VIDEO = 1;
    private static final int REQUEST_SELECT_VIDEO = 1;
    AudioManager _audioManager;
    AdaptadorCanciones adaptador;
    Cancion [] _librarySongs;
    Cancion _songSelected;
    ArrayList<Cancion> _playListMediaPlayer;
    VideoView videoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.save_playlist);

        videoView = (VideoView) findViewById(R.id.videoView);
        ImageButton executeSpoticar = (ImageButton) findViewById(R.id.spoticarButton);
        ImageButton galleryButton = (ImageButton) findViewById(R.id.galeryButton);
        videoView.setVisibility(View.GONE);


        //_librarySongs = new Cancion[] {new Cancion(R.drawable.pasarmelobien,"VOY A PASARMELO BIEN","HOMBRES G",R.raw.voyapasarmelobien), new Cancion(R.drawable.quieroestar,"DONDE QUIERO ESTAR","QUEVEDO", R.raw.dondequieroestar),new Cancion(R.drawable.pepas,"PEPAS","FARRUKO", R.raw.voyapasarmelobien)};

        /*adaptador = new AdaptadorCanciones(this);
        ListView _lstSongs = (ListView) findViewById(R.id.bibliotecaSongsView);
        _lstSongs.setAdapter(adaptador);
        _playListMediaPlayer = new ArrayList<>();

        _lstSongs.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {

                _songSelected = _librarySongs[arg2];
                _playListMediaPlayer.add(_songSelected);

            }
        });*/

        View.OnClickListener libraryButtons = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.spoticarButton:
                        OpenSpoticar();
                        break;
                    case R.id.galeryButton:
                        OpenGallery();
                        break;
                    default:
                        break;
                }
            }
        };

        executeSpoticar.setOnClickListener(libraryButtons);
        galleryButton.setOnClickListener(libraryButtons);



    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_SELECT_VIDEO && resultCode == RESULT_OK && data != null) {
            videoView.setVisibility(View.VISIBLE);
            Uri selectedVideoUri = data.getData();
            videoView.setVideoURI(selectedVideoUri);
            videoView.start();
        }
    }

    public void OpenSpoticar() {
        Intent spoticar = new Intent(this, MainActivity.class);
        startActivity(spoticar);
    }

    public void OpenGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
        intent.setType("video/*, image/*");
        startActivityForResult(intent, PICK_IMAGE_VIDEO);
    }

    class AdaptadorCanciones extends ArrayAdapter {
        Activity context;
        public AdaptadorCanciones(Activity context) {
            super(context, R.layout.listitemcanciones, _librarySongs);
            this.context = (Activity) context;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub
            LayoutInflater inflater = context.getLayoutInflater();
            View item = inflater.inflate(R.layout.listitemcanciones, null);

            TextView _nameSong = (TextView) item.findViewById(R.id.nameSong);
            _nameSong.setText(_librarySongs[position].getTitulo().toString());

            TextView _artista = (TextView) item.findViewById(R.id.artista);
            _artista.setText(_librarySongs[position].getArtista().toString());

            ImageView _songImage = (ImageView) item.findViewById(R.id.imgSong);
            _songImage.setImageResource(_librarySongs[position].getImage());
            return (item);
        }

    }
}