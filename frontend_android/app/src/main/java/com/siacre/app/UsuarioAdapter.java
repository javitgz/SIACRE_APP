package com.siacre.app;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.siacre.modelo.Usuario;

import java.util.List;

public class UsuarioAdapter extends RecyclerView.Adapter<UsuarioAdapter.ViewHolder> {

    private List<Usuario> listaUsuarios;
    private OnUsuarioClickListener listener;

    // Interfaz para manejar clicks de editar y eliminar
    public interface OnUsuarioClickListener {
        void onEditarClick(Usuario usuario);
        void onEliminarClick(Usuario usuario);
    }

    public UsuarioAdapter(List<Usuario> listaUsuarios, OnUsuarioClickListener listener) {
        this.listaUsuarios = listaUsuarios;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_user, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Usuario usuario = listaUsuarios.get(position);

        String nombreCompleto = (usuario.getNombres() != null ? usuario.getNombres() : "") + " " +
                (usuario.getApellidos() != null ? usuario.getApellidos() : "");
        holder.tvNombre.setText(nombreCompleto.trim());
        holder.tvEmail.setText(usuario.getCorreo() != null ? usuario.getCorreo() : "");

        holder.btnEdit.setOnClickListener(v -> {
            if (listener != null) listener.onEditarClick(usuario);
        });

        holder.btnDelete.setOnClickListener(v -> {
            if (listener != null) listener.onEliminarClick(usuario);
        });
    }

    @Override
    public int getItemCount() {
        return listaUsuarios != null ? listaUsuarios.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombre, tvEmail;
        ImageView ivPhoto;
        ImageButton btnEdit, btnDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.tvItemUserName);
            tvEmail = itemView.findViewById(R.id.tvItemUserEmail);
            ivPhoto = itemView.findViewById(R.id.ivUserPhoto);
            btnEdit = itemView.findViewById(R.id.btnEditUser);
            btnDelete = itemView.findViewById(R.id.btnDeleteUser);
        }
    }
}