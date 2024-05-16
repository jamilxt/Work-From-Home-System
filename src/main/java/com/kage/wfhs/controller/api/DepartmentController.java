/*
 * @Author 		 : Valhalla TKT (DAT OJT Batch II - Team III)
 * @Date 		 : 2024-04-24
 * @Time  		 : 21:00
 * @Project_Name : Work From Home System
 * @Contact      : tktvalhalla@gmail.com
 */
package com.kage.wfhs.controller.api;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kage.wfhs.dto.DepartmentDto;
import com.kage.wfhs.service.DepartmentService;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/department")
@AllArgsConstructor
public class DepartmentController {
	@Autowired
	private final DepartmentService departmentService;

	@PostMapping("/create")
    public ResponseEntity<DepartmentDto> createDepartment(@RequestBody DepartmentDto departmentDto){
        return ResponseEntity.ok(departmentService.createDepartment(departmentDto));
    }

	@PostMapping("/departmentList")
	public ResponseEntity<List<DepartmentDto>> getAllDepartment() {
		return ResponseEntity.ok(departmentService.getAllDepartment());
	}

	@PostMapping("/getDepartmentById")
	public ResponseEntity<DepartmentDto> getDepartment(@RequestBody Map<String, Long> request) {
		long id = request.get("departmentId");
		DepartmentDto departmentDto = departmentService.getDepartmentById(id);
		return ResponseEntity.ok(departmentDto);
	}

	@PostMapping("/getDepartment")
	public ResponseEntity<DepartmentDto> getDepartment(@RequestParam("departmentId") long id) {
		return ResponseEntity.ok(departmentService.getDepartmentById(id));
	}

	@PostMapping("/editDepartment")
	public ResponseEntity<String> updateDepartment(@RequestParam("departmentId") long id,
			@RequestBody DepartmentDto departmentDto) {
		departmentService.updateDepartment(id, departmentDto);
		return ResponseEntity.ok("Successfully Updated Department..");
	}

	@PostMapping("/deleteById")
	public ResponseEntity<String> deleteDepartmentById(@RequestParam("id") long id) {
		departmentService.deleteDepartmentById(id);
		return ResponseEntity.ok("Department deleted successfully");
	}

}
