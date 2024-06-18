/*
 * @Author 		 : Valhalla TKT (DAT OJT Batch II - Team III)
 * @Date 		 : 2024-04-24
 * @Time  		 : 21:00
 * @Project_Name : Work From Home System
 * @Contact      : tktvalhalla@gmail.com
 */
package com.kage.wfhs.serviceImplement;

import com.kage.wfhs.dto.RegisterFormDto;
import com.kage.wfhs.dto.UserDto;
import com.kage.wfhs.model.*;
import com.kage.wfhs.repository.CaptureRepository;
import com.kage.wfhs.repository.RegisterFormRepository;
import com.kage.wfhs.repository.UserRepository;
import com.kage.wfhs.service.ApproveRoleService;
import com.kage.wfhs.service.NotificationService;
import com.kage.wfhs.service.RegisterFormService;
import com.kage.wfhs.service.WorkFlowOrderService;
import com.kage.wfhs.util.EntityUtil;
import com.kage.wfhs.util.Helper;
import com.kage.wfhs.util.ImageUtil;
import com.kage.wfhs.util.OTPStaffIDExcelGenerator;

import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

@Service
@AllArgsConstructor
public class RegisterFormServiceImplement implements RegisterFormService {

    @Autowired
    private final RegisterFormRepository registerFormRepo;
    
    @Autowired
    private final UserRepository userRepo;
    
//    @Autowired
//    private final PositionRepository positionRepo;
    
    @Autowired
    private final ModelMapper modelMapper;
    
    @Autowired
    private final NotificationService notificationService;
    
    @Autowired
    private final CaptureRepository captureRepo;

    private final WorkFlowOrderService workFlowOrderService;

    @Autowired
    private final Helper helper;

    private final ApproveRoleService approveRoleService;

    @Override
    public void createRegisterForm(RegisterFormDto registerFormDto) throws Exception {
        User applicant = EntityUtil.getEntityById(userRepo, registerFormDto.getApplicantId());
        if(applicant.getPositionName() == null || applicant.getPositionName().isEmpty()){
            applicant.setPositionName(registerFormDto.getPositionName());
            EntityUtil.saveEntity(userRepo, applicant, "User");
        }
        RegisterForm registerForm = modelMapper.map(registerFormDto, RegisterForm.class);
        registerForm.setApplicant(registerFormDto.getApplicantId() > 0 ? EntityUtil.getEntityById(userRepo, registerFormDto.getApplicantId()) : null);
        registerForm.setRequester(registerFormDto.getRequesterId() > 0 ? EntityUtil.getEntityById(userRepo, registerFormDto.getRequesterId()) : null);
        registerForm.setSignature(ImageUtil.convertImageToBase64(registerFormDto.getSignatureInput()));
        registerForm.setStatus(Status.PENDING);
        registerFormRepo.save(registerForm);
        notificationService.savePendingNotification(registerForm.getStatus().name());

        Long formId = registerFormRepo.findLastId();
        Capture capture = new Capture();
        checkOsTypeAndSave(registerFormDto, formId, capture);
    }

    private void checkOsTypeAndSave(RegisterFormDto registerFormDto, Long formId, Capture capture) {
        capture.setOs_type(registerFormDto.getOs_type());
        if(capture.getOs_type().equalsIgnoreCase("window")){
	        capture.setAntivirusPattern(ImageUtil.convertImageToBase64(registerFormDto.getAntivirusPatternInput()));
	        capture.setAntivirusFullScan(ImageUtil.convertImageToBase64(registerFormDto.getAntivirusFullScanInput()));
        }
        capture.setOperationSystem(ImageUtil.convertImageToBase64(registerFormDto.getOperationSystemInput()));
        capture.setAntivirusSoftware(ImageUtil.convertImageToBase64(registerFormDto.getAntivirusSoftwareInput()));
        capture.setSecurityPatch(ImageUtil.convertImageToBase64(registerFormDto.getSecurityPatchInput()));

        capture.setRegisterForm(formId > 0 ? EntityUtil.getEntityById(registerFormRepo, formId) :  null);
        captureRepo.save(capture);
    }

    @Override
    public RegisterFormDto getRegisterForm(Long id) {
    	RegisterForm registerForm = EntityUtil.getEntityById(registerFormRepo, id);
    	User formRegistererUser = EntityUtil.getEntityById(userRepo, registerForm.getApplicant().getId());
    	User registerUser = userRepo.findByStaffId(formRegistererUser.getStaffId());
//    	Position formRegistererPosition = positionRepo.findById(formRegistererUser.getPosition().getId());
//    	formRegistererUser.setPosition(formRegistererPosition);
    	registerForm.setApplicant(registerUser);
        return modelMapper.map(registerForm, RegisterFormDto.class);
    }

    @Override
    public List<RegisterFormDto> getAllRegisterForm() {
        List<RegisterForm> registerForms = registerFormRepo.findAll();
        List<RegisterFormDto> registerFormList = new ArrayList<>();
        for (RegisterForm registerForm : registerForms){
            RegisterFormDto registerFormDto = modelMapper.map(registerForm, RegisterFormDto.class);
            registerFormList.add(registerFormDto);
        }
        return registerFormList;
    }

    @Override
    public Long getFormLastId() {
        return registerFormRepo.findLastId();
    }
    
    @Override
    public List<RegisterFormDto> getAllFormSpecificTeam(Long approveRoleId,String status, Long teamId) {

        List<RegisterForm> registerForms = registerFormRepo.findRegisterFormByTeam(approveRoleId,status, teamId);
        List<RegisterFormDto> registerFormList = new ArrayList<>();
        for (RegisterForm registerForm : registerForms){
            RegisterFormDto registerFormDto = modelMapper.map(registerForm, RegisterFormDto.class);
            registerFormList.add(registerFormDto);
        }
        return registerFormList;

    }

    @Override
    public List<RegisterFormDto> getAllFormSpecificDepartment(Long approveRoleId,String status, Long teamId) {

        List<RegisterForm> registerForms = registerFormRepo.findRegisterFormByDepartment(approveRoleId,status, teamId);
        List<RegisterFormDto> registerFormList = new ArrayList<>();
        for (RegisterForm registerForm : registerForms){
            RegisterFormDto registerFormDto = modelMapper.map(registerForm, RegisterFormDto.class);
            registerFormList.add(registerFormDto);
        }
        return registerFormList;
    }

    @Override
    public List<RegisterFormDto> getAllFormSpecificDivision(Long approveRoleId,String status, Long teamId) {

        List<RegisterForm> registerForms = registerFormRepo.findRegisterFormByDivision(approveRoleId,status, teamId);
        List<RegisterFormDto> registerFormList = new ArrayList<>();
        for (RegisterForm registerForm : registerForms){
            RegisterFormDto registerFormDto = modelMapper.map(registerForm, RegisterFormDto.class);
            registerFormList.add(registerFormDto);
        }
        return registerFormList;
    }

    @Override
    public List<RegisterFormDto> getAllFormSpecificTeamAll(Long approveRoleId, Long teamId) {
        List<RegisterForm> registerForms = registerFormRepo.findRegisterFormByTeamAll(approveRoleId, teamId);
        List<RegisterFormDto> registerFormList = new ArrayList<>();
        for (RegisterForm registerForm : registerForms){
            RegisterFormDto registerFormDto = modelMapper.map(registerForm, RegisterFormDto.class);
            registerFormList.add(registerFormDto);
        }
        return registerFormList;
    }

    @Override
    public List<RegisterFormDto> getAllFormSpecificDepartmentAll(Long approveRoleId, Long departmentId) {
        List<RegisterForm> registerForms = registerFormRepo.findRegisterFormByDepartmentAll(approveRoleId, departmentId);
        List<RegisterFormDto> registerFormList = new ArrayList<>();
        for (RegisterForm registerForm : registerForms){
            RegisterFormDto registerFormDto = modelMapper.map(registerForm, RegisterFormDto.class);
            registerFormList.add(registerFormDto);
        }
        return registerFormList;
    }

    @Override
    public List<RegisterFormDto> getAllFormSpecificDivisionAll(Long approveRoleId, Long divisionId) {
        List<RegisterForm> registerForms = registerFormRepo.findRegisterFormByDivisionAll(approveRoleId, divisionId);
        List<RegisterFormDto> registerFormList = new ArrayList<>();
        for (RegisterForm registerForm : registerForms){
            RegisterFormDto registerFormDto = modelMapper.map(registerForm, RegisterFormDto.class);
            registerFormList.add(registerFormDto);
        }
        return registerFormList;
    }
    
    @Override
    public List<RegisterFormDto> getAllForm(Long approveRoleId, String status) {
        List<RegisterForm> registerForms = registerFormRepo.findRegisterForm(approveRoleId, status);
        List<RegisterFormDto> registerFormList = new ArrayList<>();
        for (RegisterForm registerForm : registerForms){
            RegisterFormDto registerFormDto = modelMapper.map(registerForm, RegisterFormDto.class);
            registerFormList.add(registerFormDto);
        }
        return registerFormList;
    }
    
    @Override
    public List<RegisterFormDto> getFormAll(Long approveRoleId) {
        List<RegisterForm> registerForms = registerFormRepo.findRegisterFormAll(approveRoleId);
        List<RegisterFormDto> registerFormList = new ArrayList<>();
        for (RegisterForm registerForm : registerForms){
            RegisterFormDto registerFormDto = modelMapper.map(registerForm, RegisterFormDto.class);
            registerFormList.add(registerFormDto);
        }
        return registerFormList;
    }
    
    @Override
    public void upgradeRegisterForm(Long formId, RegisterFormDto registerFormDto) {
        RegisterForm form = EntityUtil.getEntityById(registerFormRepo, formId);
        modelMapper.map(registerFormDto, form);

        registerFormRepo.save(form);

        Capture capture = captureRepo.findByRegisterFormId(formId);
        checkOsTypeAndSave(registerFormDto, formId, capture);
    }

	@Override
	public void exportStaffIdsForOTP(List<Long> formIds, HttpServletResponse response) {
		List<RegisterForm> formList = registerFormRepo.findAllById(formIds);
        OTPStaffIDExcelGenerator generator = new OTPStaffIDExcelGenerator(formList);
        //form.setCompleteStatus(3);
        this.registerFormRepo.saveAll(formList);
        try {
            generator.export(response);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Map<String, Object> getTeamWithStatus(String status, long teamId, long userId) {
        //UserDto user = userService.getUserById(userId);
        User user = EntityUtil.getEntityById(userRepo, userId);
        ApproveRole approveRole = helper.getMaxOrder(user.getApproveRoles());
        long orderId = workFlowOrderService.getWorkFlowOrderByApproveRoleId(approveRole.getId()).getId();
        long approveRoleId = approveRoleService.getIdByWorkFlowOrderId(orderId);

        Map<String, Object> responseData = new HashMap<>();
        List<RegisterFormDto> registerFormDtoList;

        if (status.equalsIgnoreCase("ALL")) {
            registerFormDtoList = getAllFormSpecificTeamAll(approveRoleId, teamId);
        } else {
            registerFormDtoList = getAllFormSpecificTeam(approveRoleId, status, teamId);
        }

        responseData.put("forms", registerFormDtoList);

        return responseData;
    }

}