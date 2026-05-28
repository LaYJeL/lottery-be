package com.game.lottery.model;

import com.game.lottery.enums.PaymentMethodType;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "payment_methods", schema = "lottery")
public class PaymentMethod extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "method_type", nullable = false, length = 32)
    private PaymentMethodType type;

    // Назва для користувача (наприклад: "Мій гаманець Binance" або "Зарплатна
    // карта")
    @Column(name = "label", length = 100)
    private String label;

    // Публічна адреса гаманця АБО Маска карти (4111....9999) АБО Email (PayPal)
    // Це те, що ми показуємо в інтерфейсі
    @Column(name = "identifier", nullable = false, length = 255)
    private String identifier;

    // Технічний токен від платіжної системи (для карт).
    // Для крипти може бути null або те саме, що identifier
    @Column(name = "provider_token", length = 512)
    private String providerToken;

    @Column(name = "is_primary", nullable = false)
    @Builder.Default
    private boolean isPrimary = false; // Чи використовувати цей метод за замовчуванням
}
