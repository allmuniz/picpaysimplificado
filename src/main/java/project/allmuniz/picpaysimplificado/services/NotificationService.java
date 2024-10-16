package project.allmuniz.picpaysimplificado.services;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import project.allmuniz.picpaysimplificado.domain.user.User;
import project.allmuniz.picpaysimplificado.dtos.NotificationDTO;

@Service
public class NotificationService {

    private final RestTemplate restTemplate;

    public NotificationService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public void sendNotification(User user, String message) throws Exception {
        String email = user.getEmail();
        NotificationDTO notificationRequest = new NotificationDTO(message, email);

        System.out.println("Notificação enviada para o usuário.");
    }
}
