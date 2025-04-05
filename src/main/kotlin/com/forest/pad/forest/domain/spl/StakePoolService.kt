package com.forest.pad.forest.domain.spl

import com.forest.pad.forest.interfaces.api.spl.req.StakePoolReq
import com.forest.pad.forest.interfaces.api.spl.res.StakePoolRes
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.io.File
import kotlin.io.path.createTempFile

@Service
class StakePoolService {
    private val logger = LoggerFactory.getLogger(this::class.java)
    private val sanctumMintAuthority = "GRwm4EXMyVwtftQeTft7DZT3HBRxx439PrKq4oM6BwoZ"

    fun update() {
        println("Epoch run")
    }

    fun tokenMint(req: StakePoolReq): StakePoolRes {
        var metadataFile: File? = null

        try {
            // 메타데이터 파일 생성
            metadataFile = createMetadataFile(req)

            // 생성된 메타데이터 파일 내용 로깅
            val metadataContent = metadataFile.readText()
            logger.info("생성된 메타데이터 파일 내용: $metadataContent")
            logger.info("메타데이터 파일 경로: ${metadataFile.absolutePath}")

            // metaboss 명령어 실행
            val command = listOf(
                "metaboss", "create", "fungible",
                "--decimals", "9",
                "--metadata", metadataFile.absolutePath
            )

            logger.info("Executing: ${command.joinToString(" ")}")

            // 프로세스 실행 및 결과 처리
            val process = ProcessBuilder(command)
                .redirectErrorStream(true)
                .start()

            val output = process.inputStream.bufferedReader().use { it.readText() }
            val exitCode = process.waitFor()

            // 결과 처리
            return if (exitCode == 0) {
                // 출력에서 값 추출
                val transactionId = extractValue(output, "Signature: ")
                val tokenAddress = extractValue(output, "Mint: ")
                val metadataAddress = extractValue(output, "Metadata: ")

                // 토큰 정보 저장
                if (tokenAddress != null) {
                    // Freeze 권한 비활성화
                    val disableFreezeCommand = listOf(
                        "spl-token", "authorize",
                        "--disable",
                        tokenAddress,
                        "freeze"
                    )

                    logger.info("Disabling freeze authority: ${disableFreezeCommand.joinToString(" ")}")

                    val freezeProcess = ProcessBuilder(disableFreezeCommand)
                        .redirectErrorStream(true)
                        .start()

                    val freezeOutput = freezeProcess.inputStream.bufferedReader().use { it.readText() }
                    val freezeExitCode = freezeProcess.waitFor()

                    if (freezeExitCode == 0) {
                        logger.info("Freeze authority disabled successfully")
                    } else {
                        logger.error("Failed to disable freeze authority: $freezeOutput")
                    }

                    // Mint 권한 Sanctum으로 전송
                    val transferMintCommand = listOf(
                        "spl-token", "authorize",
                        tokenAddress,
                        "mint",
                        sanctumMintAuthority
                    )

                    logger.info("Transferring mint authority: ${transferMintCommand.joinToString(" ")}")

                    val mintProcess = ProcessBuilder(transferMintCommand)
                        .redirectErrorStream(true)
                        .start()

                    val mintOutput = mintProcess.inputStream.bufferedReader().use { it.readText() }
                    val mintExitCode = mintProcess.waitFor()

                    if (mintExitCode == 0) {
                        logger.info("Mint authority transferred successfully")
                    } else {
                        logger.error("Failed to transfer mint authority: $mintOutput")
                    }
                }

                StakePoolRes(
                    msg = "Token created successfully",
                    tokenAddress = tokenAddress,
                    metadataAddress = metadataAddress,
                    transactionId = transactionId
                )
            } else {
                logger.error("명령 실행 실패: $output")
                StakePoolRes(
                    msg = "Failed to create token: ${output.take(100)}"
                )
            }

        } catch (e: Exception) {
            logger.error("토큰 생성 중 오류 발생", e)
            return StakePoolRes(
                msg = "Error: ${e.message}"
            )
        } finally {
            try {
                // 디버깅이 끝나면 파일 삭제 주석 해제
                metadataFile?.delete()
            } catch (e: Exception) {
                logger.warn("임시 파일 삭제 실패: ${e.message}")
            }
        }
    }

    private fun extractValue(output: String, prefix: String): String? {
        return output.lines()
            .find { it.trim().startsWith(prefix) }
            ?.substringAfter(prefix)
            ?.trim()
    }

    private fun createMetadataFile(req: StakePoolReq): File {
        val tempFile = createTempFile(prefix = "metadata-", suffix = ".json").toFile()

        // 간단한 테스트 메타데이터로 시도
        val metadata = """
        {
            "name": "${req.name}",
            "symbol": "${req.symbol}",
            "signature": "forest_pad_official", 
            "originTokenAddress": "${req.originTokenAddress}",
            "uri": "${req.uri}",
            "seller_fee_basis_points": ${req.sellerFeeBasisPoints},
            "creators": []
        }
        """.trimIndent()

        tempFile.writeText(metadata)
        logger.info("메타데이터 파일 생성: ${tempFile.absolutePath}")
        return tempFile
    }
}