package com.pulpapp.ms_users.service;

import com.pulpapp.ms_users.dto.PedidoDTO;
import com.pulpapp.ms_users.entity.Pedido;
import com.pulpapp.ms_users.entity.User;
import com.pulpapp.ms_users.repository.PedidoRepository;
import com.pulpapp.ms_users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PedidoServiceImpl implements IPedidoService {

    private final PedidoRepository pedidoRepository;
    private final UserRepository userRepository;

    @Override
    public PedidoDTO save(PedidoDTO dto) {

        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Pedido pedido = new Pedido();
        pedido.setDescripcion(dto.getDescripcion());
        pedido.setTotal(dto.getTotal());
        pedido.setUser(user);

        Pedido saved = pedidoRepository.save(pedido);

        return mapToDTO(saved);
    }

    @Override
    public List<PedidoDTO> findAll() {
        return pedidoRepository.findAll()
                .stream()
                .map(this::mapToDTO)
                .toList();
    }

    @Override
    public PedidoDTO findById(Long id) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pedido not found"));
        return mapToDTO(pedido);
    }

    @Override
    public void delete(Long id) {
        pedidoRepository.deleteById(id);
    }

    private PedidoDTO mapToDTO(Pedido pedido) {
        PedidoDTO dto = new PedidoDTO();
        dto.setId(pedido.getId());
        dto.setDescripcion(pedido.getDescripcion());
        dto.setTotal(pedido.getTotal());
        dto.setUserId(pedido.getUser().getId());
        return dto;
    }
}