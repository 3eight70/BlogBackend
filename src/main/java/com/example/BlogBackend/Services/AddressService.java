package com.example.BlogBackend.Services;

import com.example.BlogBackend.Mappers.GarMapper;
import com.example.BlogBackend.Models.Exceptions.ExceptionResponse;
import com.example.BlogBackend.Models.Gar.*;
import com.example.BlogBackend.Repositories.AddressRepository;
import com.example.BlogBackend.Repositories.HierarchyRepository;
import com.example.BlogBackend.Repositories.HouseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AddressService {
    private final AddressRepository addressRepository;
    private final HouseRepository houseRepository;
    private final HierarchyRepository hierarchyRepository;

    public ResponseEntity<?> searchAddress(Long parentObjectId, String query) {
        List<AsAdmHierarchy> hierarchies = hierarchyRepository.findAllByParentobjid(parentObjectId);
        if (hierarchies == null) {
            return new ResponseEntity<>(new ExceptionResponse(HttpStatus.NOT_FOUND.value(),
                    "Адреса с таким parentObjectId не существует"), HttpStatus.NOT_FOUND);
        }
        List<GarResponse> result = new ArrayList<>();

        for (AsAdmHierarchy hierarchy : hierarchies) {
            Long hierarchyId = hierarchy.getObjectid();

            AsAddrObj address = addressRepository.findByObjectidAndIsactiveAndIsactual(hierarchyId);
            AsHouse house = houseRepository.findByObjectidAndIsactiveAndIsactual(hierarchyId);

            if (address != null) {
                if (query != null) {
                    if (address.getName().contains(query)) {
                        result.add(GarMapper.addressToGarResponse(address));
                    }
                } else {
                    result.add(GarMapper.addressToGarResponse(address));
                }
            } else if (house != null) {
                if (query != null) {
                    if (house.getHousenum().contains(query)) {
                        result.add(GarMapper.houseToGarResponse(house));
                    }
                } else {
                    result.add(GarMapper.houseToGarResponse(house));
                }
            }
        }

        setObjectLevels(result);

        if (query == null) {
            return ResponseEntity.ok(result.subList(0, Math.min(10, result.size())));
        }

        return ResponseEntity.ok(result);
    }

    public ResponseEntity<?> getAddressChain(UUID objectGuid){
        AsAddrObj address = addressRepository.findByObjectguidAndIsactiveAndIsactual(objectGuid);
        AsHouse house = houseRepository.findByObjectguidAndIsactiveAndIsactual(objectGuid);

        if (address == null && house == null){
            return new ResponseEntity<>(new ExceptionResponse(
                    HttpStatus.NOT_FOUND.value(),
                    "Адреса с данным objectGuid не существует"
            ), HttpStatus.NOT_FOUND);
        }

        AsAddrObj addressFromHierarchy = null;
        AsHouse houseFromHierarchy = null;

        if (address != null){
            addressFromHierarchy = addressRepository.findByObjectidAndIsactiveAndIsactual(address.getObjectid());
        }
        else if (house != null){
            houseFromHierarchy = houseRepository.findByObjectidAndIsactiveAndIsactual(house.getObjectid());
        }

        List<AsAdmHierarchy> addressList = new ArrayList<>();

        if (houseFromHierarchy == null && addressFromHierarchy != null){
            addressList = getPath(addressList, addressFromHierarchy.getObjectid());
        }
        else if (houseFromHierarchy != null && addressFromHierarchy == null){
            addressList = getPath(addressList, houseFromHierarchy.getObjectid());
        }
        List<GarResponse> result = new ArrayList<>();

        for (AsAdmHierarchy hierarchy : addressList){
            AsAddrObj curAddress = addressRepository.findByObjectidAndIsactiveAndIsactual(hierarchy.getObjectid());
            AsHouse curHouse = houseRepository.findByObjectidAndIsactiveAndIsactual(hierarchy.getObjectid());

            if (curAddress != null){
                result.add(GarMapper.addressToGarResponse(curAddress));
            }
            else if (curHouse != null){
                result.add(GarMapper.houseToGarResponse(curHouse));
            }
        }

        setObjectLevels(result);

        return ResponseEntity.ok(result);
    }

    private void setObjectLevels(List<GarResponse> result){
        for (GarResponse res : result) {
            AddressObjectLevel objLevel = addressObjectLevels(res.getObjectLevelText());
            res.objectLevel = objLevel.getObjectLevel();
            res.objectLevelText = objLevel.getObjectLevelText();
        }
    }

    private List<AsAdmHierarchy> getPath(List<AsAdmHierarchy> addressList, Long objectId){
        AsAdmHierarchy curObject = hierarchyRepository.findByObjectid(objectId);
        if (curObject == null){
            return addressList;
        }
        addressList.add(curObject);

        AsAdmHierarchy parentObject = hierarchyRepository.findByObjectid(curObject.getParentobjid());
        if (parentObject == null){
            return addressList;
        }
        getPath(addressList, parentObject.getObjectid());

        return addressList;
    }

    private AddressObjectLevel addressObjectLevels(String level) {
        switch (level) {
            case "1":
                return new AddressObjectLevel(
                        GarAddressLevel.Region,
                        "Субъект РФ"
                );
            case "2":
                return new AddressObjectLevel(
                        GarAddressLevel.AdministrativeArea,
                        "Административный район"
                );
            case "3":
                return new AddressObjectLevel(
                        GarAddressLevel.MunicipalArea,
                        "Муниципальный район"
                );
            case "4":
                return new AddressObjectLevel(
                        GarAddressLevel.RuralUrbanSettlement,
                        "Сельское/городское поселение"
                );
            case "5":
                return new AddressObjectLevel(
                        GarAddressLevel.City,
                        "Город"
                );
            case "6":
                return new AddressObjectLevel(
                        GarAddressLevel.Locality,
                        "Населенный пункт"
                );
            case "7":
                return new AddressObjectLevel(
                        GarAddressLevel.ElementOfPlanningStructure,
                        "Элемент планировочной структуры"
                );
            case "8":
                return new AddressObjectLevel(
                        GarAddressLevel.ElementOfRoadNetwork,
                        "Элемент улично-дорожной сети"
                );
            case "9":
                return new AddressObjectLevel(
                        GarAddressLevel.Land,
                        "Земельный участок"
                );
            case "10":
                return new AddressObjectLevel(
                        GarAddressLevel.Building,
                        "Здание (сооружение)"
                );
            case "11":
                return new AddressObjectLevel(
                        GarAddressLevel.Room,
                        "Помещение"
                );
            case "12":
                return new AddressObjectLevel(
                        GarAddressLevel.RoomInRooms,
                        "Помещения в пределах помещения"
                );
            case "13":
                return new AddressObjectLevel(
                        GarAddressLevel.AutonomousRegionLevel,
                        "Уровень автономного округа"
                );
            case "14":
                return new AddressObjectLevel(
                        GarAddressLevel.IntracityLevel,
                        "Уровень внутригородской территории"
                );
            case "15":
                return new AddressObjectLevel(
                        GarAddressLevel.AdditionalTerritoriesLevel,
                        "Уровень дополнительных территорий  РФ"
                );
            case "16":
                return new AddressObjectLevel(
                        GarAddressLevel.LevelOfObjectsInAdditionalTerritories,
                        "Уровень объектов на дополнительных территориях"
                );
            case "17":
                return new AddressObjectLevel(
                        GarAddressLevel.CarPlace,
                        "Машиноместо");
            default:
                return new AddressObjectLevel(
                        GarAddressLevel.Building,
                        "Здание (сооружение)");
        }
    }
}
