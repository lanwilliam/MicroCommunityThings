package com.java110.things.service.car;

import com.java110.things.entity.accessControl.HeartbeatTaskDto;
import com.java110.things.entity.accessControl.UserFaceDto;
import com.java110.things.entity.machine.MachineDto;
import io.netty.channel.ChannelHandlerContext;

import java.util.List;

/**
 * 门禁处理接口类，各大门禁厂商 需要实现这个类，实现相应的方法
 * add by wuxw
 * <p>
 * 2020-05-11
 */
public interface ICarProcess {

    /**
     * 初始化方法
     */
    void initCar();

    /**
     * 查询设备中 人脸数量
     *
     * @return 返回人脸数
     */
    int getCarNum();

    /**
     * 根据设备编码和 faceId 查询是否有人脸
     * @param userFaceDto 用户信息
     * @return 如果有人脸 返回 faceId,没有则返回 null
     */
    String getCar(UserFaceDto userFaceDto);


    /**
     * 添加修改车牌
     * @param userFaceDto 用户人脸信息
     */
    void addAndUpdateCar(UserFaceDto userFaceDto);


    /**
     * 删除车牌
     * @param heartbeatTaskDto 任务ID
     */
    void deleteCar(HeartbeatTaskDto heartbeatTaskDto);


    /**
     * 重启设备
     * @param machineDto 硬件信息
     */
    void restartMachine(MachineDto machineDto);

    /**
     * 开门
     * @param machineDto  硬件信息
     */
    void openDoor(MachineDto machineDto);

    /**
     * 人脸推送结果
     * @param data 这个为设备人脸推送协议，请参考设备协议文档
     * @return 处理成功时返回 true 失败时返回false
     */
    String httpFaceResult(String data);

    /**
     * 接受消息
     * @param content
     * @return
     */
    Long accept(String content, ChannelHandlerContext ctx);



}