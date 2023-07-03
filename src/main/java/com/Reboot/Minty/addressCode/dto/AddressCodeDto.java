package com.Reboot.Minty.addressCode.dto;

import com.Reboot.Minty.addressCode.entity.AddressCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.modelmapper.ModelMapper;

@Getter
@Setter
@NoArgsConstructor
public class AddressCodeDto {

    private Long id;
    private String code;
    private String sido;
    private String sigungu;
    private String dong;
    private String lat;
    private String lon;

    private static ModelMapper modelMapper = new ModelMapper();

    public static AddressCodeDto of(AddressCode addressCode){
        return modelMapper.map(addressCode, AddressCodeDto.class);
    }
}
