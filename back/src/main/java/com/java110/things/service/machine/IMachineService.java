package com.java110.things.service.machine;

import com.java110.things.entity.machine.MachineDto;
import com.java110.things.entity.response.ResultDto;
import com.java110.things.entity.user.UserDto;

import java.util.List;


/**
 * @ClassName IUserService
 * @Description TODO
 * @Author wuxw
 * @Date 2020/5/14 14:48
 * @Version 1.0
 * add by wuxw 2020/5/14
 **/
public interface IMachineService {

    /**
     * 保存设备信息
     * @param machineDto 设备信息
     * @return
     * @throws Exception
     */
    ResultDto saveMachine(MachineDto machineDto) throws Exception;
    /**
     * 保存设备信息
     * @param machineDto 设备信息
     * @return
     * @throws Exception
     */
    ResultDto updateMachine(MachineDto machineDto) throws Exception;

    /**
     * 获取设备信息
     * @param machineDto 设备信息
     * @return
     * @throws Exception
     */
    ResultDto getMachine(MachineDto machineDto) throws Exception;

    /**
     * 获取设备信息
     * @param machineDto 设备信息
     * @return
     * @throws Exception
     */
    List<MachineDto> queryMachines(MachineDto machineDto) throws Exception;

    /**
     * 删除设备
     * @param machineDto 设备信息
     * @return
     * @throws Exception
     */
    ResultDto deleteMachine(MachineDto machineDto) throws Exception;

    /**
     * 重启设备
     * @param machineDto 设备信息
     * @return
     * @throws Exception
     */
    ResultDto restartMachine(MachineDto machineDto) throws Exception;


    /**
     * 开门
     * @param machineDto 设备信息
     * @return
     * @throws Exception
     */
    ResultDto openDoor(MachineDto machineDto) throws Exception;
}
