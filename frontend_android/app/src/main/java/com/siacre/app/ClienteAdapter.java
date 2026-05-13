package com.siacre.app;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.siacre.modelo.Cliente;

import java.util.List;

public class ClienteAdapter extends RecyclerView.Adapter<ClienteAdapter.ViewHolder> {

    private List<Cliente> listaClientes;
    private OnClienteClickListener listener;

    public interface OnClienteClickListener {
        void onEditarClick(Cliente cliente);
        void onEliminarClick(Cliente cliente);
    }

    public ClienteAdapter(List<Cliente> listaClientes, OnClienteClickListener listener) {
        this.listaClientes = listaClientes;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_cliente, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Cliente cliente = listaClientes.get(position);
        String nombreCompleto = (cliente.getNombres() != null ? cliente.getNombres() : "") + " " +
                (cliente.getApellidos() != null ? cliente.getApellidos() : "");
        holder.tvNombre.setText(nombreCompleto.trim());
        holder.tvDocumento.setText("CC: " + cliente.getDocumento());

        holder.btnEdit.setOnClickListener(v -> {
            if (listener != null) listener.onEditarClick(cliente);
        });

        holder.btnDelete.setOnClickListener(v -> {
            if (listener != null) listener.onEliminarClick(cliente);
        });
    }

    @Override
    public int getItemCount() {
        return listaClientes != null ? listaClientes.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombre, tvDocumento;
        ImageButton btnEdit, btnDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.tvItemNombre);
            tvDocumento = itemView.findViewById(R.id.tvItemDocumento);
            btnEdit = itemView.findViewById(R.id.btnEditCliente);
            btnDelete = itemView.findViewById(R.id.btnDeleteCliente);
        }
    }
}