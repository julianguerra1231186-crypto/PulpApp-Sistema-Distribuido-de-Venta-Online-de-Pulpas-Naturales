package com.pulpapp.ms_users.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/**
 * Configuración visual (branding) por tenant.
 * Cada tenant puede personalizar su logo, colores y banner.
 */
@Entity
@Table(name = "tenant_config")
@Getter
@Setter
public class TenantConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tenant_id", nullable = false, unique = true)
    private Long tenantId;

    @Column(name = "logo_url")
    private String logoUrl;

    @Column(name = "primary_color", length = 20)
    private String primaryColor;

    @Column(name = "secondary_color", length = 20)
    private String secondaryColor;

    @Column(name = "banner_url")
    private String bannerUrl;
}
