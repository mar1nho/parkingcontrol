package com.parkingapi.parkingcontrol.services;

import com.parkingapi.parkingcontrol.dtos.ParkingSpotDTO;
import com.parkingapi.parkingcontrol.models.ParkingSpotModel;
import com.parkingapi.parkingcontrol.repositories.ParkingSpotRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ParkingSpotService {

    final
    ParkingSpotRepository repository;

    public ParkingSpotService(ParkingSpotRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public ResponseEntity<List<ParkingSpotModel>>  findAll() {
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(repository.findAll());
    }

    @Transactional
    public ResponseEntity<Object> save(ParkingSpotDTO parkingSpotDTO) {
        if (existByLicensePlateCar(parkingSpotDTO.getLicensePlateCar())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Plate Car Already in use");
        } if (existsByParkingSpotNumber(parkingSpotDTO.getParkingSpotNumber())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Spot Already in Use");
        } if (existsByApartmentAndBlock(parkingSpotDTO.getApartment(), parkingSpotDTO.getBlock())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Apartment already have car");
        }
        var parkingSpotModel = new ParkingSpotModel();
        BeanUtils.copyProperties(parkingSpotDTO, parkingSpotModel);
        parkingSpotModel.setRegistrationDate(LocalDateTime.now(ZoneId.of("UTC")));
        return ResponseEntity.status(HttpStatus.CREATED).body(repository.save(parkingSpotModel));
    }

    @Transactional
    public ResponseEntity<?> getOneParkingSpot(@PathVariable("id") UUID id){
        Optional<ParkingSpotModel> psModel = repository.findById(id);
        if(psModel.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Parking Spot Not Found");
        }
        return ResponseEntity.status(HttpStatus.OK).body(psModel.get());
    }


    @Transactional
    public ResponseEntity<?> getSpotByNumber(@PathVariable("parking_spot_number") String parking_spot_number) {
        ParkingSpotModel parkingSpotModelOptional = repository.findByParkingSpotNumber(parking_spot_number);
        if (parkingSpotModelOptional == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Spot doesn't have an owner or doesn't exist.");
        }
        return ResponseEntity.status(HttpStatus.OK).body(repository.findByParkingSpotNumber(parking_spot_number));
    }

    @Transactional
    public ResponseEntity<?> deleteParkingSpot(@PathVariable("id") UUID id){
        Optional<ParkingSpotModel> parkingSpotModel = repository.findById(id);
        if (!parkingSpotModel.isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Spot doesn't have an owner or doesn't exist.");
        }
        repository.delete(parkingSpotModel.get());
        return ResponseEntity.status(HttpStatus.OK).body("Parking Spot deleted");
    }

    @Transactional
    public ResponseEntity<?> updateParkingSpot(@PathVariable("id") UUID id, @RequestBody @Valid ParkingSpotDTO parkingSpotDTO){
        Optional<ParkingSpotModel> parkingSpotModelOptional = repository.findById(id);
        if (!parkingSpotModelOptional.isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Parking Spot Not Found!");
        }
        var parkingSpotModel = new ParkingSpotModel();

        BeanUtils.copyProperties(parkingSpotDTO, parkingSpotModel);
        parkingSpotModel.setId(parkingSpotModelOptional.get().getId());
        parkingSpotModel.setRegistrationDate(parkingSpotModelOptional.get().getRegistrationDate());

        return ResponseEntity.status(HttpStatus.OK).body(repository.save(parkingSpotModel));
    }

    @Transactional
    public ResponseEntity<Page<ParkingSpotModel>> findAllPageable(Pageable pageable){
        return ResponseEntity.status(HttpStatus.OK).body(repository.findAll(pageable));
    }



    @Transactional
    public Boolean existByLicensePlateCar(String licensePlateCar) {
        return repository.existsByLicensePlateCar(licensePlateCar);
    }
    @Transactional
    public Boolean existsByParkingSpotNumber(String parkingSpotNumber) {
        return repository.existsByParkingSpotNumber(parkingSpotNumber);
    }
    @Transactional
    public Boolean existsByApartmentAndBlock(String apartment, String block){
        return repository.existsByApartmentAndBlock(apartment, block);
    }
}
