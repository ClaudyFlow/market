<template>
  <div class="page-container">
    <header class="page-header">
      <h1 class="page-title">
        <el-icon><Shop /></el-icon>
        商家管理
      </h1>
    </header>

    <section class="stats-cards">
      <el-row :gutter="15">
        <el-col :span="6">
          <div class="stat-card primary">
            <div class="stat-icon"><el-icon><Shop /></el-icon></div>
            <div class="stat-info">
              <div class="stat-value">{{ merchantStats.total }}</div>
              <div class="stat-label">商家总数</div>
            </div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="stat-card success">
            <div class="stat-icon"><el-icon><CircleCheck /></el-icon></div>
            <div class="stat-info">
              <div class="stat-value">{{ merchantStats.approved }}</div>
              <div class="stat-label">已审核</div>
            </div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="stat-card warning">
            <div class="stat-icon"><el-icon><Clock /></el-icon></div>
            <div class="stat-info">
              <div class="stat-value">{{ merchantStats.pending }}</div>
              <div class="stat-label">待审核</div>
            </div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="stat-card danger">
            <div class="stat-icon"><el-icon><CircleClose /></el-icon></div>
            <div class="stat-info">
              <div class="stat-value">{{ merchantStats.rejected }}</div>
              <div class="stat-label">已拒绝</div>
            </div>
          </div>
        </el-col>
      </el-row>
    </section>

    <section class="search-bar">
      <el-form :inline="true" :model="filterForm">
        <el-form-item label="商家 ID">
          <el-input v-model="filterForm.merchantId" placeholder="请输入商家 ID" clearable style="width: 150px" />
        </el-form-item>
        <el-form-item label="店铺名称">
          <el-input v-model="filterForm.shopName" placeholder="请输入店铺名称" clearable style="width: 180px" />
        </el-form-item>
        <el-form-item label="审核状态">
          <el-select v-model="filterForm.status" placeholder="请选择状态" clearable style="width: 120px">
            <el-option label="待审核" value="pending" />
            <el-option label="已通过" value="approved" />
            <el-option label="已拒绝" value="rejected" />
            <el-option label="已封禁" value="banned" />
          </el-select>
        </el-form-item>
        <el-form-item label="入驻时间">
          <el-date-picker
            v-model="filterForm.dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            style="width: 240px"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="searchMerchants">搜索</el-button>
          <el-button @click="resetFilter">重置</el-button>
        </el-form-item>
      </el-form>
    </section>

    <section class="table-section">
      <el-table :data="merchantList" class="sci-table" style="width: 100%" v-loading="loading">
        <el-table-column prop="id" label="商家 ID" width="100" />
        <el-table-column prop="logo" label="店铺 Logo" width="80">
          <template #default="{ row }">
            <el-avatar :size="50" shape="square" :src="row.logo || `https://via.placeholder.com/50x50/00d4ff/fff?text=店`" />
          </template>
        </el-table-column>
        <el-table-column prop="shopName" label="店铺名称" min-width="150" />
        <el-table-column prop="ownerName" label="店主姓名" width="100" />
        <el-table-column prop="phone" label="联系电话" width="130" />
        <el-table-column prop="category" label="主营类目" width="120">
          <template #default="{ row }">
            <el-tag size="small" type="info">{{ row.category }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="joinTime" label="入驻时间" width="140" />
        <el-table-column prop="status" label="状态" width="90">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)" size="small">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="250" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" text size="small" @click="viewMerchant(row)">详情</el-button>
            <el-button
              v-if="row.status === 'pending'"
              type="success"
              text
              size="small"
              @click="approveMerchant(row)"
            >
              通过
            </el-button>
            <el-button
              v-if="row.status === 'pending'"
              type="danger"
              text
              size="small"
              @click="rejectMerchant(row)"
            >
              拒绝
            </el-button>
            <el-button
              v-if="row.status === 'approved'"
              type="warning"
              text
              size="small"
              @click="banMerchant(row)"
            >
              封禁
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-bar">
        <el-pagination
          v-model:current-page="pagination.currentPage"
          v-model:page-size="pagination.pageSize"
          :total="pagination.total"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="loadMerchantList"
          @current-change="loadMerchantList"
        />
      </div>
    </section>

    <el-dialog
      v-model="auditDialog.visible"
      title="商家入驻审核"
      width="700px"
      :close-on-click-modal="false"
    >
      <el-descriptions :column="2" border class="sci-descriptions">
        <el-descriptions-item label="商家 ID">{{ currentMerchant?.id }}</el-descriptions-item>
        <el-descriptions-item label="店铺名称">{{ currentMerchant?.shopName }}</el-descriptions-item>
        <el-descriptions-item label="店主姓名">{{ currentMerchant?.ownerName }}</el-descriptions-item>
        <el-descriptions-item label="联系电话">{{ currentMerchant?.phone }}</el-descriptions-item>
        <el-descriptions-item label="主营类目">{{ currentMerchant?.category }}</el-descriptions-item>
        <el-descriptions-item label="入驻时间">{{ currentMerchant?.joinTime }}</el-descriptions-item>
        <el-descriptions-item label="店铺简介" :span="2">{{ currentMerchant?.description }}</el-descriptions-item>
      </el-descriptions>
      
      <div class="cert-section">
        <h4>资质证照</h4>
        <div class="cert-images">
          <el-image
            :src="currentMerchant?.businessLicense || 'https://via.placeholder.com/200x130/1a2a4a/00d4ff?text=营业执照'"
            class="cert-image"
            fit="cover"
            :preview-src-list="[currentMerchant?.businessLicense || 'https://via.placeholder.com/200x130/1a2a4a/00d4ff?text=营业执照']"
          />
          <el-image
            :src="currentMerchant?.license || 'https://via.placeholder.com/200x130/1a2a4a/00d4ff?text=许可证'"
            class="cert-image"
            fit="cover"
            :preview-src-list="[currentMerchant?.license || 'https://via.placeholder.com/200x130/1a2a4a/00d4ff?text=许可证']"
          />
        </div>
      </div>

      <el-form :model="auditForm" label-width="80px" style="
  background: linear-gradient(180deg, rgba(0, 212, 255, 0.05) 0%, transparent 100%);
  min-height: 100vh;
}

.page-header {
  
  padding: 20px;
  background: linear-gradient(135deg, rgba(26, 31, 58, 0.9), rgba(26, 31, 58, 0.7));
  border: 1px solid rgba(0, 212, 255, 0.2);
  border-radius: 12px;
  box-shadow: 0 4px 20px rgba(0, 212, 255, 0.1);
}

.page-title {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 22px;
  font-weight: bold;
  color: #fff;
}

.page-title .el-icon {
  color: var(--mall-primary);
  font-size: 26px;
  filter: drop-shadow(0 0 8px rgba(0, 212, 255, 0.5));
}

.stats-cards {
  
}

.stat-card {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 20px;
  background: linear-gradient(135deg, rgba(26, 31, 58, 0.8), rgba(26, 31, 58, 0.6));
  border: 1px solid rgba(0, 212, 255, 0.15);
  border-radius: 12px;
  transition: all 0.3s;
  cursor: pointer;
  position: relative;
  overflow: hidden;
}

.stat-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 2px;
  background: linear-gradient(90deg, transparent, var(--glow-color), transparent);
}

.stat-card:hover {
  transform: translateY(-3px);
  border-color: var(--glow-color);
  box-shadow: 0 8px 30px rgba(0, 212, 255, 0.15);
}

.stat-card.primary { --glow-color: #00d4ff; }
.stat-card.success { --glow-color: #00ff88; }
.stat-card.warning { --glow-color: #ffaa00; }
.stat-card.danger { --glow-color: #ff6666; }

.stat-icon {
  width: 50px;
  height: 50px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  color: #fff;
  flex-shrink: 0;
}

.stat-card.primary .stat-icon {
  background: linear-gradient(135deg, #00d4ff, #00a8cc);
  box-shadow: 0 0 15px rgba(0, 212, 255, 0.4);
}

.stat-card.success .stat-icon {
  background: linear-gradient(135deg, #00ff88, #00cc6a);
  box-shadow: 0 0 15px rgba(0, 255, 136, 0.4);
}

.stat-card.warning .stat-icon {
  background: linear-gradient(135deg, #ffaa00, #ff8800);
  box-shadow: 0 0 15px rgba(255, 170, 0, 0.4);
}

.stat-card.danger .stat-icon {
  background: linear-gradient(135deg, #ff6666, #ff4444);
  box-shadow: 0 0 15px rgba(255, 102, 102, 0.4);
}

.stat-info {
  flex: 1;
}

.stat-value {
  font-size: 24px;
  font-weight: bold;
  color: #fff;
}

.stat-label {
  font-size: 13px;
  color: #888;
  
}

/* 搜索栏优化 */
.search-bar {
  background: linear-gradient(135deg, rgba(26, 31, 58, 0.8), rgba(26, 31, 58, 0.6));
  border: 1px solid rgba(0, 212, 255, 0.2);
  border-radius: 12px;
  padding: 20px;
  
  box-shadow: 0 4px 20px rgba(0, 212, 255, 0.08);
}

.search-bar :deep(.el-form-item) {
  
  
}

.search-bar :deep(.el-form-item__label) {
  color: #ccc;
  font-weight: 500;
}

.search-bar :deep(.el-input__wrapper) {
  background: rgba(10, 14, 26, 0.6);
  border: 1px solid rgba(0, 212, 255, 0.15);
  border-radius: 8px;
  padding: 8px 12px;
  transition: all 0.3s;
}

.search-bar :deep(.el-input__wrapper:hover) {
  border-color: rgba(0, 212, 255, 0.3);
}

.search-bar :deep(.el-input__wrapper.is-focus) {
  border-color: var(--mall-primary);
  box-shadow: 0 0 15px rgba(0, 212, 255, 0.2);
}

.search-bar :deep(.el-input__inner) {
  color: #fff;
}

.search-bar :deep(.el-select .el-input__wrapper) {
  background: rgba(10, 14, 26, 0.6);
}

.search-bar :deep(.el-select__wrapper) {
  color: #fff;
}

.search-bar :deep(.el-date-editor .el-input__wrapper) {
  background: rgba(10, 14, 26, 0.6);
}

.search-bar :deep(.el-button--primary) {
  background: linear-gradient(135deg, #00d4ff, #00ff88);
  border: none;
  box-shadow: 0 0 15px rgba(0, 212, 255, 0.4);
  color: #000;
  font-weight: bold;
  padding: 10px 20px;
}

.search-bar :deep(.el-button--primary:hover) {
  box-shadow: 0 0 25px rgba(0, 212, 255, 0.6);
  transform: translateY(-2px);
}

.search-bar :deep(.el-button) {
  border-radius: 8px;
}

/* 表格区域 */
.table-section {
  background: linear-gradient(135deg, rgba(26, 31, 58, 0.8), rgba(26, 31, 58, 0.6));
  border: 1px solid rgba(0, 212, 255, 0.15);
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 4px 20px rgba(0, 212, 255, 0.08);
}

.pagination-bar {
  
  display: flex;
  justify-content: flex-end;
}

.sci-table :deep(.el-table__header th) {
  background: rgba(0, 212, 255, 0.08);
  color: var(--mall-primary);
  font-size: 13px;
  border-bottom: 1px solid rgba(0, 212, 255, 0.15);
}

.sci-table :deep(.el-table__body td) {
  background: transparent;
  color: #aaa;
  border-bottom: 1px solid rgba(255, 255, 255, 0.05);
  font-size: 13px;
}

.sci-table :deep(.el-table__row:hover) {
  background: rgba(0, 212, 255, 0.05);
}

.cert-section {
  
}

.cert-section h4 {
  color: var(--mall-primary);
  
  font-size: 14px;
}

.cert-images {
  display: flex;
  gap: 15px;
}

.cert-image {
  width: 200px;
  height: 130px;
  border-radius: 8px;
  cursor: pointer;
  border: 1px solid rgba(0, 212, 255, 0.2);
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.3);
}

.cert-image:hover {
  transform: scale(1.05);
  box-shadow: 0 4px 20px rgba(0, 212, 255, 0.3);
}

.sci-descriptions {
  --el-descriptions-bg-color: transparent;
}

.sci-descriptions :deep(.el-descriptions__label) {
  background: rgba(0, 212, 255, 0.08);
  color: var(--mall-primary);
}

.sci-descriptions :deep(.el-descriptions__content) {
  color: #aaa;
}
</style>
