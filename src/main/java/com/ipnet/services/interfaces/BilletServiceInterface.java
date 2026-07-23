package com.ipnet.services.interfaces;

import com.ipnet.dto.BilletDto;
import java.util.List;
import java.util.UUID;

public interface BilletServiceInterface {
    BilletDto validerBillet(String qrCode);
    List<BilletDto> getBilletsByTrajet(UUID trajetId);
}