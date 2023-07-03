package com.parkingapi.parkingcontrol.controllers;

import com.parkingapi.parkingcontrol.dtos.ParkingSpotDTO;
import com.parkingapi.parkingcontrol.models.ParkingSpotModel;
import com.parkingapi.parkingcontrol.services.ParkingSpotService;
import jakarta.validation.Valid;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;

@Controller
@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("/condominio")  
public class ParkingSpotController {

    final
    ParkingSpotService parkingSpotService;

    public ParkingSpotController(ParkingSpotService parkingSpotService) {
        this.parkingSpotService = parkingSpotService;
    }

    @GetMapping("/parkingspots")
    public ResponseEntity<List<ParkingSpotModel>> getAllParkingSpots(){
        return parkingSpotService.findAll();
    }

    @GetMapping("/parkingspots/pageable")
    public ResponseEntity<Page<ParkingSpotModel>> getAllParkingSpotsPageable(@PageableDefault(page =0, size = 5, sort = "id", direction = Sort.Direction.ASC) Pageable pageable){
        return parkingSpotService.findAllPageable(pageable);
    }

    @PostMapping("/save")
    public ResponseEntity<?> saveParkingSpot(@RequestBody @Valid ParkingSpotDTO parkingSpotDTO){
        return parkingSpotService.save(parkingSpotDTO);
    }

    @GetMapping("/getspotbyid/{id}")
    public ResponseEntity<?> getOneParkingSpot(@PathVariable("id") UUID id){
        return parkingSpotService.getOneParkingSpot(id);
    }

    @GetMapping("/getspotbynumber/{parking_spot_number}")
    public ResponseEntity<?> getOneParkingSpot(@PathVariable("parking_spot_number") String parking_spot_number){
        return parkingSpotService.getSpotByNumber(parking_spot_number);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteParkingSpot(@PathVariable("id") UUID id){
        return parkingSpotService.deleteParkingSpot(id);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateParkingSpot(@PathVariable("id") UUID id, @RequestBody @Valid ParkingSpotDTO parkingSpotDTO){
        return parkingSpotService.updateParkingSpot(id, parkingSpotDTO);
    }

}
