package com.las.message;

/**
 * @version 1.0
 * @Description
 * @Author：andy
 * @CreateDate：2016/4/7
 */
public enum NaluType {

    /**
     *
     0	未指定
     1	一个非IDR图像的编码条带slice_layer_without_partitioning_rbsp( )
     2	编码条带数据分割块Aslice_data_partition_a_layer_rbsp( )
     3	编码条带数据分割块Bslice_data_partition_b_layer_rbsp( )
     4	编码条带数据分割块Cslice_data_partition_c_layer_rbsp( )
     5	IDR图像的编码条带slice_layer_without_partitioning_rbsp( )
     6	辅助增强信息 (SEI)sei_rbsp( )
     7	序列参数集seq_parameter_set_rbsp( )
     8	图像参数集pic_parameter_set_rbsp( )
     9	访问单元分隔符access_unit_delimiter_rbsp( )
     10	序列结尾end_of_seq_rbsp( )
     11	流结尾end_of_stream_rbsp( )
     12	填充数据filler_data_rbsp( )
     13	序列参数集扩展seq_parameter_set_extension_rbsp( )
     14...18	保留
     19	未分割的辅助编码图像的编码条带slice_layer_without_partitioning_rbsp( )
     20...23	保留
     24...31	未指定
     */

    /**
     *NALU_TYPE_SLICE 1
     * NALU_TYPE_DPA 2
     * NALU_TYPE_DPB 3
     * NALU_TYPE_DPC 4
     * NALU_TYPE_IDR 5
     * NALU_TYPE_SEI 6
     * NALU_TYPE_SPS 7
     * NALU_TYPE_PPS 8
     * NALU_TYPE_AUD 9
     * NALU_TYPE_EOSEQ 10
     * NALU_TYPE_EOSTREAM 11
     * NALU_TYPE_FILL 12
     */
}
