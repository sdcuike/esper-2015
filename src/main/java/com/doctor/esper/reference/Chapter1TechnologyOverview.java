package com.doctor.esper.reference;

/**
 * Chapter 1. Technology Overview
 * 
 ***** 1.关系数据库及其sql设计的目地主要是为了数据相对静态和复杂查询比较少的应用（OLTP，磁盘数据的存取有相应的优化结构，当然内存数据库，数据存放在内存内）。
 * 对于cep系统来说，内存数据库更接近其目地。
 * 
 ***** 2.cep引擎的实现和关系数据库的实现有点相反，数据库一般把数据存放到磁盘（静态的），我们通过网络把sql流传给数据库引擎，操作数据库，返回结果流，流向应用。
 * 而cep则是把查询表达式存储，数据事件流流向其中。其实查询表达式中的窗口函数及表达式等组合起来，实现了一种条件性数据结构（时间范围或符合条件的数据才能流向其中存储）。
 * 
 ***** 3.cep提供了两种机制处理事件：事件模式和事件流查询。
 * 
 * 
 * @author doctor
 *
 * @time 2015年6月1日 上午11:39:33
 */
public class Chapter1TechnologyOverview {

	public static void main(String[] args) {

	}

}
