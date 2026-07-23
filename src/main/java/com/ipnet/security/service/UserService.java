package com.ipnet.security.service;

import com.ipnet.security.enums.StatutCompte;
import com.ipnet.security.exception.response.AuthenticationResponse;
import com.ipnet.security.dto.*;

import java.util.List;
import java.util.UUID;

public interface UserService {

    AuthenticationResponse authenticate(LoginDTO loginDTO);
    UserDTO saveUser(UserDTO userDTO);
    List<UserRoleReponse> getAllUsers();
    UserRoleReponse getUserById(Long id);
    UserDTO updateUser(UserDTO userDTO, UUID id);
    UserDTO updateMyInfo(UUID id, SelfUpdateDTO dto);
    void deleteUserById(UUID id);
    UserDTO changerStatutCompte(UUID id, StatutCompte statutCompte);
    UserDTO updatePassword(UUID id, PasswordDTO passwordDTO);
    String resetPasswordByAdmin(UUID id);
    void forgotPassword(ForgotPasswordRequestDTO dto);
    void resetPassword(ResetPasswordDTO dto);
    List<HistoryReponse> getAllHistory();
    List<RoleDTO> getAllRoles();
    List<UserRoleReponse> getChauffeurs();
    List<UserRoleReponse> getLivreurs();
    List<UserRoleReponse> getChauffeursByVille(UUID villeId);
    List<UserRoleReponse> getLivreursByVille(UUID villeId);
}
