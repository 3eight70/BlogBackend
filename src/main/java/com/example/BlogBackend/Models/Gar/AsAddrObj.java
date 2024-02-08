package com.example.BlogBackend.Models.Gar;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "as_addr_obj", schema = "fias")
public class AsAddrObj {
    @Id
    @Column(name = "id", nullable = false)
    private Long id;

    @NotNull
    @Column(name = "objectid", nullable = false)
    private Long objectid;

    @NotNull
    @Column(name = "objectguid", nullable = false)
    private UUID objectguid;

    @Column(name = "changeid")
    private Long changeid;

    @NotNull
    @Column(name = "name", nullable = false, length = Integer.MAX_VALUE)
    private String name;

    @Column(name = "typename", length = Integer.MAX_VALUE)
    private String typename;

    @NotNull
    @Column(name = "level", nullable = false, length = Integer.MAX_VALUE)
    private String level;

    @Column(name = "opertypeid")
    private Integer opertypeid;

    @Column(name = "previd")
    private Long previd;

    @Column(name = "nextid")
    private Long nextid;

    @Column(name = "updatedate")
    private LocalDate updatedate;

    @Column(name = "startdate")
    private LocalDate startdate;

    @Column(name = "enddate")
    private LocalDate enddate;

    @Column(name = "isactual")
    private Integer isactual;

    @Column(name = "isactive")
    private Integer isactive;

}