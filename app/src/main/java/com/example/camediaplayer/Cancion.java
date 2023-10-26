package com.example.camediaplayer;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.widget.ImageView;

import java.net.URI;

public class Cancion implements Parcelable {
	private int image;
	private String titulo;
	private String artista;
	private int uri;
	
	public Cancion(int img, String tit, String art, int u) {
		image = img;
		titulo=tit;
		artista=art;
		uri = u;
	}

	public int getImage() {
		return image;
	}

	public void setImage(int image) {
		this.image = image;
	}

	public int getUri() {
		return uri;
	}

	public void setUri(int uri) {
		this.uri = uri;
	}

	public String getTitulo(){
		return titulo;
	}
	
	public String getArtista(){
		return artista;
	}

	public void setTitulo(String t){
		this.titulo = t;
	}

	public void setArtista(String s){
		this.artista = s;
	}


	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel parcel, int i) {

	}
}