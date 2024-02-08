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
@Table(name = "as_houses", schema = "fias")
public class AsHouse {
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

    @Column(name = "housenum", length = Integer.MAX_VALUE)
    private String housenum;

    @Column(name = "addnum1", length = Integer.MAX_VALUE)
    private String addnum1;

    @Column(name = "addnum2", length = Integer.MAX_VALUE)
    private String addnum2;

    @Column(name = "housetype")
    private Integer housetype;

    @Column(name = "addtype1")
    private Integer addtype1;

    @Column(name = "addtype2")
    private Integer addtype2;

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