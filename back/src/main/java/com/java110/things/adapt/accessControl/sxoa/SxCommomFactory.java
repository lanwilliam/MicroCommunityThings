package com.java110.things.adapt.accessControl.sxoa;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.java110.things.entity.accessControl.UserFaceDto;
import com.java110.things.entity.community.CommunityDto;
import com.java110.things.entity.machine.MachineDto;
import com.java110.things.factory.LocalCacheFactory;
import com.java110.things.factory.MappingCacheFactory;
import com.java110.things.service.community.ICommunityService;
import com.java110.things.util.Assert;
import com.java110.things.util.BeanConvertUtil;
import com.java110.things.util.DateUtil;
import com.java110.things.util.StringUtil;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.List;

public class SxCommomFactory {


    public static final String GET_TOKEN = "/v1.0/token";
    public static final String GET_SIGN = "/v1.0/sign";
    public static final String GET_AREA_CODE = "/v1.0/area/page";
    public static final String GET_COMMUNITY = "/v1.0/village/view";
    public static final String ADD_COMMUNITY = "/v1.0/village/create";
    public static final String FACEFEATURE = "/v1.0/facefeature/create";

    public static final String FACE_URL = "ACCESS_CONTROL_FACE_URL";
    //图片后缀
    public static final String IMAGE_SUFFIX = ".jpg";


    public static SxAreaCodeDto getSxAreaCode(RestTemplate outRestTemplate) {
        JSONObject paramIn = new JSONObject();
        paramIn.put("fullName", "安康市");

        HttpEntity httpEntity = new HttpEntity(paramIn.toJSONString(), getHeader(outRestTemplate));
        ResponseEntity<String> responseEntity = outRestTemplate.exchange(MappingCacheFactory.getValue("SXOA_URL") + GET_AREA_CODE, HttpMethod.POST, httpEntity, String.class);

        if (responseEntity.getStatusCode() != HttpStatus.OK) {
            throw new IllegalStateException("请求sign失败" + responseEntity);
        }

        JSONObject paramOut = JSONObject.parseObject(responseEntity.getBody());

        if (paramOut.getInteger("code") != 0) {
            throw new IllegalStateException("请求sign失败" + paramOut);
        }

        JSONArray resultList = paramOut.getJSONArray("resultList");

        if (resultList == null || resultList.size() < 1) {
            throw new IllegalArgumentException("未找到 区域信息");
        }

        SxAreaCodeDto sxAreaCodeDto = BeanConvertUtil.covertBean(resultList.getJSONObject(0), SxAreaCodeDto.class);

        return sxAreaCodeDto;
    }

    public static SxCommunityDto getSxCommunity(RestTemplate outRestTemplate, String communityId, ICommunityService communityService) {
        JSONObject paramIn = new JSONObject();
        paramIn.put("id", communityId);
        paramIn.put("type", 2);

        HttpEntity httpEntity = new HttpEntity(paramIn.toJSONString(), getHeader(outRestTemplate));
        ResponseEntity<String> responseEntity = outRestTemplate.exchange(MappingCacheFactory.getValue("SXOA_URL") + GET_COMMUNITY, HttpMethod.POST, httpEntity, String.class);

        if (responseEntity.getStatusCode() != HttpStatus.OK) {
            throw new IllegalStateException("请求sign失败" + responseEntity);
        }

        JSONObject paramOut = JSONObject.parseObject(responseEntity.getBody());

        if (paramOut.getInteger("code") != 0) {
            return null;
        }

        if (paramOut.containsKey("data")) {
            return null;
        }

        SxCommunityDto sxCommunityDto = BeanConvertUtil.covertBean(paramOut.getJSONObject("data"), SxCommunityDto.class);

        CommunityDto communityDto = new CommunityDto();
        communityDto.setCommunityId(communityId);
        List<CommunityDto> communityDtos = communityService.queryCommunitys(communityDto);

        Assert.listOnlyOne(communityDtos, "小区不存在");
        sxCommunityDto.setLocationId(communityDtos.get(0).getThirdCommunityId());

        return sxCommunityDto;
    }

    public static void addSxCommunity(RestTemplate outRestTemplate, MachineDto machineDto, ICommunityService communityService) {
        JSONObject paramIn = new JSONObject();
        paramIn.put("viAddress", machineDto.getCommunityId());
        paramIn.put("viCode", machineDto.getCommunityId());
        paramIn.put("viName", machineDto.getCommunityId());
        paramIn.put("viAreaCode", getSxAreaCode(outRestTemplate).getAcode());

        HttpEntity httpEntity = new HttpEntity(paramIn.toJSONString(), getHeader(outRestTemplate));
        ResponseEntity<String> responseEntity = outRestTemplate.exchange(MappingCacheFactory.getValue("SXOA_URL") + ADD_COMMUNITY, HttpMethod.POST, httpEntity, String.class);

        if (responseEntity.getStatusCode() != HttpStatus.OK) {
            throw new IllegalStateException("请求sign失败" + responseEntity);
        }

        JSONObject paramOut = JSONObject.parseObject(responseEntity.getBody());

        if (paramOut.getInteger("code") != 0) {
            throw new IllegalStateException("添加小区失败" + responseEntity);
        }

        String thirdCommunityId = paramOut.getJSONObject("data").getJSONObject("group").getString("locationId");

        CommunityDto communityDto = new CommunityDto();
        communityDto.setCommunityId(machineDto.getCommunityId());
        communityDto.setThirdCommunityId(thirdCommunityId);
        try {
            communityService.updateCommunity(communityDto);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public static FaceFeatureResultDto facefeature(RestTemplate outRestTemplate, MachineDto machineDto, ICommunityService communityService, UserFaceDto userFaceDto) {
        JSONObject paramIn = new JSONObject();
        paramIn.put("accessToken", machineDto.getCommunityId());
        paramIn.put("ffResidentId", userFaceDto.getUserId());
        paramIn.put("ffOrgId", getSxCommunity(outRestTemplate, machineDto.getCommunityId(), communityService).getViOrgId());
        paramIn.put("fileUrl", MappingCacheFactory.getValue(FACE_URL) + "/" + machineDto.getCommunityId() + "/" + userFaceDto.getUserId() + IMAGE_SUFFIX);

        HttpEntity httpEntity = new HttpEntity(paramIn.toJSONString(), getHeader(outRestTemplate));
        ResponseEntity<String> responseEntity = outRestTemplate.exchange(MappingCacheFactory.getValue("SXOA_URL") + FACEFEATURE, HttpMethod.POST, httpEntity, String.class);

        if (responseEntity.getStatusCode() != HttpStatus.OK) {
            throw new IllegalStateException("请求sign失败" + responseEntity);
        }

        JSONObject paramOut = JSONObject.parseObject(responseEntity.getBody());

        if (paramOut.getInteger("code") != 0) {
            throw new IllegalStateException("添加小区失败" + responseEntity);
        }

        FaceFeatureResultDto faceFeatureResultDto = BeanConvertUtil.covertBean(paramOut.getJSONObject("data"), FaceFeatureResultDto.class);

        return faceFeatureResultDto;

    }

    public static boolean hasSxCommunity(RestTemplate outRestTemplate, String communityId, ICommunityService communityService) {
        if (getSxCommunity(outRestTemplate, communityId, communityService) != null) {
            return true;
        }
        return false;
    }

    public static HttpHeaders getHeader(RestTemplate outRestTemplate) {
        return getHeader(outRestTemplate, 0);
    }

    public static HttpHeaders getHeader(RestTemplate outRestTemplate, int length) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("X-AUTH-TOKEN", getToken(outRestTemplate));
        httpHeaders.add("Content-Type", "application/json");
        httpHeaders.add("X-EQUIP-TYPE", "dog");
        if (length > 0) {
            httpHeaders.add("Content-Length", length + "");
        }
        return httpHeaders;
    }

    /**
     * 获取accessToken
     *
     * @return
     */
    public static String getToken(RestTemplate outRestTemplate) {

        String token = LocalCacheFactory.getValue("sxoa_token");
        if (!StringUtil.isEmpty(token)) {
            return token;
        }
        String orgKey = MappingCacheFactory.getValue("orgKey");
        String orgSecret = MappingCacheFactory.getValue("orgSecret");

        long timestamp = DateUtil.getCurrentDate().getTime();
        JSONObject paramIn = new JSONObject();
        paramIn.put("orgKey", orgKey);
        paramIn.put("orgSecret", orgSecret);
        paramIn.put("timestamp", timestamp);
        // 查询sign
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Content-Type", "application/json");
        HttpEntity httpEntity = new HttpEntity(paramIn.toJSONString(), httpHeaders);
        ResponseEntity<String> responseEntity = outRestTemplate.exchange(MappingCacheFactory.getValue("SXOA_URL") + GET_SIGN, HttpMethod.POST, httpEntity, String.class);

        if (responseEntity.getStatusCode() != HttpStatus.OK) {
            throw new IllegalStateException("请求sign失败" + responseEntity);
        }

        JSONObject paramOut = JSONObject.parseObject(responseEntity.getBody());

        if (paramOut.getInteger("code") != 2000) {
            throw new IllegalStateException("请求sign失败" + paramOut);
        }

        String sign = paramOut.getString("data");

        paramIn = new JSONObject();
        paramIn.put("orgKey", orgKey);
        paramIn.put("orgSecret", orgSecret);
        paramIn.put("timestamp", timestamp + "");
        paramIn.put("sign", sign);
        // 查询sign
        httpHeaders = new HttpHeaders();
        httpHeaders.add("Content-Type", "application/json");
        httpEntity = new HttpEntity(paramIn.toJSONString(), httpHeaders);
        responseEntity = outRestTemplate.exchange(MappingCacheFactory.getValue("SXOA_URL") + GET_TOKEN, HttpMethod.POST, httpEntity, String.class);

        if (responseEntity.getStatusCode() != HttpStatus.OK) {
            throw new IllegalStateException("请求sign失败" + responseEntity);
        }

        paramOut = JSONObject.parseObject(responseEntity.getBody());

        if (paramOut.getInteger("code") != 2000) {
            throw new IllegalStateException(paramOut.getString("msg"));
        }

        token = paramOut.getJSONObject("data").getString("token");
        LocalCacheFactory.setValue("sxoa_token", token, 10 * 24 * 60);
        return token;
    }
}
