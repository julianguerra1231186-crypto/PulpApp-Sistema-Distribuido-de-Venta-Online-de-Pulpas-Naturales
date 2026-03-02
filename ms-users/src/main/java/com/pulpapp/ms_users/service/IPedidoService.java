package com.pulpapp.ms_users.service;

import com.pulpapp.ms_users.dto.PedidoDTO;
import java.util.List;

public interface IPedidoService {

    PedidoDTO save(PedidoDTO dto);

    List<PedidoDTO> findAll();

    PedidoDTO findById(Long id);

    void delete(Long id);
}